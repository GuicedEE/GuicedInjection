/*
 * Copyright (C) 2017 Marc Magon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jwebmp.guicedinjection;

import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.jwebmp.guicedinjection.interfaces.*;
import com.jwebmp.guicedinjection.threading.PostStartupRunnable;
import com.jwebmp.logger.LogFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides an interface for reflection and injection in one.
 * <p>
 * Use reflect() to access the class library, or inject() to get the injector for any instance
 *
 * @author GedMarc
 * @version 1.0
 * @since Nov 14, 2016
 */
public class GuiceContext
		implements Serializable
{

	private static final Logger log = LogFactory.getLog("GuiceContext");
	private static final long serialVersionUID = 1L;

	/**
	 * This particular instance of the class
	 */
	private static final GuiceContext instance = new GuiceContext();

	/**
	 * The building injector
	 */
	private static boolean buildingInjector = false;

	private static int threadCount = Runtime.getRuntime()
	                                        .availableProcessors();

	private static long asyncTerminationWait = 60L;
	private static TimeUnit asyncTerminationTimeUnit = TimeUnit.SECONDS;
	/**
	 * The configuration object
	 */
	private static GuiceConfig<?> config;
	/**
	 * The physical injector for the JVM container
	 */
	private transient Injector injector;
	/**
	 * The actual scanner
	 */
	private transient ClassGraph scanner;
	/**
	 * The scan result built from everything - the core scanner.
	 */
	private transient ScanResult scanResult;
	/**
	 * Facade layer for backwards compatibility
	 */
	private transient Reflections reflections;

	/**
	 * Creates a new Guice context. Not necessary
	 */
	private GuiceContext()
	{
		//No config required
	}

	/**
	 * Reference the Injector Directly
	 *
	 * @return The global Guice Injector Object, Never Null, Instantiates the Injector if not configured
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static synchronized Injector inject()
	{
		if (buildingInjector)
		{
			throw new RuntimeException(
					"The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.");
		}
		if (instance().injector == null)
		{
			try
			{
				buildingInjector = true;
				log.info("Starting up Guice Context");
				instance().loadPreStartups();
				instance().loadConfiguration();
				if (instance().getConfig()
				              .isWhiteList() ||
				    instance().getConfig()
				              .isClasspathScanning())
				{
					instance().loadScanner();
				}
				List cModules = instance().loadDefaultBinders();
				instance().injector = Guice.createInjector(cModules);
				buildingInjector = false;
				instance().loadPostStartups();
				log.config("Injection System Ready");
			}
			catch (Throwable e)
			{
				log.log(Level.SEVERE, "Exception creating Injector : " + e.getMessage(), e);
			}
		}
		buildingInjector = false;
		return instance().injector;
	}

	private void loadPreStartups()
	{
		ServiceLoader<IGuicePreStartup> preStartups = ServiceLoader.load(IGuicePreStartup.class);
		List<IGuicePreStartup> startups = new ArrayList<>();
		for (IGuicePreStartup preStartup : preStartups)
		{
			startups.add(preStartup);
		}
		startups.sort(Comparator.comparing(IGuicePreStartup::sortOrder));
		for (IGuicePreStartup startup : startups)
		{
			log.config("Loading IGuicePreStartup - " +
			           startup.getClass()
			                  .getCanonicalName());
			startup.onStartup();
		}
	}

	private void loadConfiguration()
	{
		ServiceLoader<IGuiceConfigurator> guiceConfigurators = ServiceLoader.load(IGuiceConfigurator.class);
		if (GuiceContext.config == null)
		{
			GuiceContext.config = new GuiceConfig<>();
		}
		for (IGuiceConfigurator guiceConfigurator : guiceConfigurators)
		{
			log.config("Loading IGuiceConfigurator - " +
			           guiceConfigurator.getClass()
			                            .getCanonicalName());
			GuiceContext.config = guiceConfigurator.configure(config);
		}
		log.config("IGuiceConfigurator Final Configuration : " + config.toString());
	}

	/**
	 * Starts up Guice and the scanner
	 */
	private void loadScanner()
	{
		Stopwatch stopwatch = Stopwatch.createStarted();
		log.info("Loading Classpath Scanner - [" + getThreadCount() + "] threads");
		if (config == null)
		{
			loadConfiguration();
		}
		scanner = new ClassGraph();
		if (config.isWhiteList())
		{
			String[] packages = getPackagesList();
			if (packages.length != 0)
			{
				scanner.whitelistPackages(packages);
			}
			String[] paths = getPathsList();
			if (paths.length != 0)
			{
				scanner.whitelistPaths(paths);
			}
			scanner.blacklistPaths(getPathsBlacklistList());
		}
		if (config.isFieldInfo())
		{
			scanner.enableFieldInfo();
		}
		if (config.isAnnotationScanning())
		{
			scanner.enableAnnotationInfo();
		}
		if (config.isMethodInfo())
		{
			scanner.enableMethodInfo();
		}
		if (config.isIgnoreFieldVisibility())
		{
			scanner.ignoreFieldVisibility();
		}
		if (config.isIgnoreMethodVisibility())
		{
			scanner.ignoreMethodVisibility();
		}

		if (config.isVerbose())
		{
			scanner.verbose();
		}
		try
		{
			scanResult = scanner.scan(getThreadCount());
			Map<String, ResourceList.ByteArrayConsumer> fileScans = quickScanFiles();
			fileScans.forEach((key, value) ->
					                  scanResult.getResourcesWithLeafName(key)
					                            .forEachByteArray(value));
		}
		catch (Exception mpe)
		{
			log.log(Level.SEVERE, "Unable to run scanner", mpe);
		}

		stopwatch.stop();
		log.fine("Loaded Classpath Scanner -Took [" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "] millis.");
	}

	@SuppressWarnings("unchecked")
	private List loadDefaultBinders()
	{
		ServiceLoader<IGuiceModule> preStartups = ServiceLoader.load(IGuiceModule.class);
		List<IGuiceModule> startups = new ArrayList<>();
		for (IGuiceModule preStartup : preStartups)
		{
			startups.add(preStartup);
		}
		startups.sort(Comparator.comparing(IGuiceModule::sortOrder));
		List output = new ArrayList<>();
		for (IGuiceModule startup : startups)
		{
			log.config("Loading IGuiceModule  - " +
			           startup.getClass()
			                  .getCanonicalName());
			output.add(startup);
		}
		return output;
	}

	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>
	 * 		The type to retrieve
	 * @param type
	 * 		The physical class object
	 *
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T getInstance(@NotNull Class<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>
	 * 		The type to retrieve
	 * @param type
	 * 		The physical class object
	 *
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T get(@NotNull Class<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * Gets a new specified instance from a give key
	 *
	 * @param <T>
	 * 		The type to retrieve
	 * @param type
	 * 		The physical class object
	 *
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T getInstance(@NotNull Key<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * Gets a new specified instance from a give key
	 *
	 * @param <T>
	 * 		The type to retrieve
	 * @param type
	 * 		The physical class object
	 *
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T get(@NotNull Key<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * Returns the async termination wait period Default 60
	 *
	 * @return The wait time for post startup operations to finish
	 */
	@SuppressWarnings("unused")
	public static long getAsyncTerminationWait()
	{
		return asyncTerminationWait;
	}

	/**
	 * Sets the termination asynchronous wait period (60)
	 *
	 * @param asyncTerminationWait
	 * 		The wait time for post startup threads to finish
	 */
	@SuppressWarnings("unused")
	public static void setAsyncTerminationWait(long asyncTerminationWait)
	{
		GuiceContext.asyncTerminationWait = asyncTerminationWait;
	}

	/**
	 * Gets the termination waiting period (Defualt sesonds)
	 *
	 * @return The wait time for post startup object wait time
	 */
	@SuppressWarnings("unused")
	public static TimeUnit getAsyncTerminationTimeUnit()
	{
		return asyncTerminationTimeUnit;
	}

	/**
	 * Sets the asynchronous termination waiting period
	 *
	 * @param asyncTerminationTimeUnit
	 * 		The unit to apply for the waiting time
	 */
	@SuppressWarnings("unused")
	public static void setAsyncTerminationTimeUnit(TimeUnit asyncTerminationTimeUnit)
	{
		GuiceContext.asyncTerminationTimeUnit = asyncTerminationTimeUnit;
	}

	/**
	 * Execute on Destroy
	 */
	@SuppressWarnings("unused")
	public static void destroy()
	{
		instance().reflections = null;
		instance().scanResult = null;
		instance().scanner = null;
		instance().injector = null;
	}

	/**
	 * Returns the actual context instance, provides access to methods existing a bit deeper
	 *
	 * @return The singleton instance of this
	 */
	public static GuiceContext instance()
	{
		return instance;
	}

	/**
	 * Returns the current scan result
	 *
	 * @return The physical Scan Result from the complete class scanner
	 */
	@NotNull
	public ScanResult getScanResult()
	{
		if (scanResult == null)
		{
			loadScanner();
		}
		return scanResult;
	}

	private void loadPostStartups()
	{
		ServiceLoader<IGuicePostStartup> postStartups = ServiceLoader.load(IGuicePostStartup.class);
		Map<Integer, List<IGuicePostStartup>> postStartupGroups = new TreeMap<>();

		for (IGuicePostStartup postStartup : postStartups)
		{
			IGuicePostStartup injected = GuiceContext.getInstance(postStartup.getClass());
			Integer sortOrder = injected.sortOrder();
			postStartupGroups.computeIfAbsent(sortOrder, k -> new ArrayList<>())
			                 .add(injected);
		}
		postStartupGroups.forEach((key, value) ->
		                          {
			                          value.sort(Comparator.comparing(IGuicePostStartup::sortOrder));
			                          if (value.size() == 1)
			                          {
				                          log.config("Loading IGuicePostStartup - " +
				                                     value.get(0)
				                                          .getClass()
				                                          .getCanonicalName());
				                          value.get(0)
				                               .postLoad();
			                          }
			                          else
			                          {
				                          List<PostStartupRunnable> runnables = new ArrayList<>();
				                          configureWorkStealingPool(value, runnables);
			                          }
		                          });
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPathsBlacklistList()
	{
		Set<String> strings = new LinkedHashSet<>();
		ServiceLoader<IPathContentsBlacklistScanner> exclusions = ServiceLoader.load(IPathContentsBlacklistScanner.class);
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPathContentsBlacklistScanner exclusion : exclusions)
			{
				log.log(Level.CONFIG, "Loading IPathContentsBlacklistScanner - " +
				                      exclusion.getClass()
				                               .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			log.log(Level.FINE, "IPathContentsBlacklistScanner Final Configuration - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPackagesList()
	{
		Set<String> strings = new LinkedHashSet<>();
		ServiceLoader<IPackageContentsScanner> exclusions = ServiceLoader.load(IPackageContentsScanner.class);
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPackageContentsScanner exclusion : exclusions)
			{
				log.log(Level.CONFIG, "Loading IPackageContentsScanner - " +
				                      exclusion.getClass()
				                               .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			log.log(Level.FINE, "IPackageScanningContentsScanner Final Configuration - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPathsList()
	{
		Set<String> strings = new LinkedHashSet<>();
		ServiceLoader<IPathContentsScanner> exclusions = ServiceLoader.load(IPathContentsScanner.class);
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPathContentsScanner exclusion : exclusions)
			{
				log.log(Level.CONFIG, "Loading IPathScanningContentsScanner - " +
				                      exclusion.getClass()
				                               .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			log.log(Level.FINE, "IPathScanningContentsScanner Final Configuration - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Registers the quick scan files
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ResourceList.ByteArrayConsumer> quickScanFiles()
	{
		Map<String, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
		ServiceLoader<IFileContentsScanner> fileScanners = ServiceLoader.load(IFileContentsScanner.class);
		for (IFileContentsScanner fileScanner : fileScanners)
		{
			log.log(Level.CONFIG, "Loading IFileContentsScanner - " +
			                      fileScanner.getClass()
			                                 .getCanonicalName());
			fileScans.putAll(fileScanner.onMatch());
		}
		return fileScans;
	}

	/**
	 * Builds an asynchronous running pool to execute with a termination waiter
	 *
	 * @param st
	 * 		A list of startup objects
	 * @param runnables
	 * 		A list of post startup threads
	 */
	private static void configureWorkStealingPool(List<IGuicePostStartup> st, List<PostStartupRunnable> runnables)
	{
		ExecutorService postLoaderExecutionService = Executors.newWorkStealingPool(threadCount);
		for (IGuicePostStartup IGuicePostStartup : st)
		{
			runnables.add(new PostStartupRunnable(IGuicePostStartup));
		}
		for (PostStartupRunnable a : runnables)
		{
			try
			{
				postLoaderExecutionService.submit((Runnable) a);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Unable to invoke Post Startups\n", e);
			}
		}
		postLoaderExecutionService.shutdown();
		try
		{
			postLoaderExecutionService.awaitTermination(asyncTerminationWait, asyncTerminationTimeUnit);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Could not execute asynchronous post loads", e);
		}
	}

	/**
	 * Gets the number of threads to use when processing
	 * Default processors count
	 *
	 * @return Default processors count
	 */
	@SuppressWarnings("all")
	public static int getThreadCount()
	{
		return threadCount;
	}

	/**
	 * Sets the thread count to use
	 *
	 * @param threadCount
	 * 		The thread count to execute on
	 */
	@SuppressWarnings("unused")
	public static void setThreadCount(int threadCount)
	{
		GuiceContext.threadCount = threadCount;
	}

	/**
	 * Sets the current scan result
	 *
	 * @param scanResult
	 * 		The physical Scan Result from the complete class scanner
	 */
	@SuppressWarnings("unused")
	public void setScanResult(ScanResult scanResult)
	{
		GuiceContext.instance().scanResult = scanResult;
	}

	/**
	 * Returns the current classpath scanner
	 *
	 * @return Default processors count
	 */
	@SuppressWarnings("unused")
	public ClassGraph getScanner()
	{
		return scanner;
	}

	/**
	 * Sets the classpath scanner
	 *
	 * @param scanner
	 * 		Sets the scanner to a specific instance
	 */
	@SuppressWarnings("unused")
	public static void setScanner(ClassGraph scanner)
	{
		instance().scanner = scanner;
	}

	/**
	 * Builds a reflection object if one does not exist
	 *
	 * @return A facade of the ReflectUtils on the scan result
	 */
	public static Reflections reflect()
	{
		if (instance().reflections == null)
		{
			instance().reflections = new Reflections();
		}
		return instance().reflections;
	}

	/**
	 * Returns the Guice Config Instance
	 *
	 * @return The singleton Guice Config instance. Also available with @Inject
	 */
	public GuiceConfig<?> getConfig()
	{
		return config;
	}
}
