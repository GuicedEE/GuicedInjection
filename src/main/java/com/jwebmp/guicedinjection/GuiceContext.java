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
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.guicedinjection.interfaces.IDefaultService.*;

/**
 * Provides an interface for reflection and injection in one.
 * <p>
 * Use reflect() to access the class library, or inject() to get the injector for any instance
 *
 * @author GedMarc
 * @version 1.0
 * @since Nov 14, 2016
 */
@SuppressWarnings("MissingClassJavaDoc")
public class GuiceContext
{

	/**
	 * Field log
	 */
	private static final Logger log = LogFactory.getLog("GuiceContext");
	/**
	 * This particular instance of the class
	 */
	private static final GuiceContext instance = new GuiceContext();
	/**
	 * A list of all the loaded singleton sets
	 */
	private static final Map<Class, Set> allLoadedServices = new ConcurrentHashMap<>();
	/**
	 * The building injector
	 */
	private static boolean buildingInjector = false;
	/**
	 * The number of threads
	 */
	private static int threadCount = Runtime.getRuntime()
	                                        .availableProcessors();
	/**
	 * The time in units to wait
	 */
	private static long asyncTerminationWait = 60L;
	/**
	 * The time in chrono to wait
	 */
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
		if (GuiceContext.buildingInjector)
		{
			throw new RuntimeException(
					"The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.");
		}
		if (GuiceContext.instance().injector == null)
		{
			try
			{
				GuiceContext.buildingInjector = true;
				GuiceContext.log.info("Starting up Guice Context");
				GuiceContext.instance()
				            .loadPreStartups();
				GuiceContext.instance()
				            .loadConfiguration();
				if (GuiceContext.instance()
				                .getConfig()
				                .isWhiteList() ||
				    GuiceContext.instance()
				                .getConfig()
				                .isClasspathScanning())
				{
					GuiceContext.instance()
					            .loadScanner();
				}
				List cModules = GuiceContext.instance()
				                            .loadDefaultBinders();
				GuiceContext.instance().injector = Guice.createInjector(cModules);
				GuiceContext.buildingInjector = false;
				GuiceContext.instance()
				            .loadPostStartups();

				Runtime.getRuntime()
				       .addShutdownHook(new Thread(GuiceContext::destroy));

				GuiceContext.log.config("Injection System Ready");
			}
			catch (Throwable e)
			{
				GuiceContext.log.log(Level.SEVERE, "Exception creating Injector : " + e.getMessage(), e);
				throw new RuntimeException("Unable to boot Guice Injector", e);
			}
		}
		GuiceContext.buildingInjector = false;
		return GuiceContext.instance().injector;
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
		return GuiceContext.inject()
		                   .getInstance(type);
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
		return GuiceContext.inject()
		                   .getInstance(type);
	}

	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>
	 * 		The type to retrieve
	 * @param type
	 * 		The physical class object
	 * @param annotation
	 * 		The annotation to fetch
	 *
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T get(@NotNull Class<T> type, Class<? extends Annotation> annotation)
	{
		return GuiceContext.inject()
		                   .getInstance(Key.get(type, annotation));
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
		return GuiceContext.inject()
		                   .getInstance(type);
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
		return GuiceContext.inject()
		                   .getInstance(type);
	}

	/**
	 * Returns the async termination wait period Default 60
	 *
	 * @return The wait time for post startup operations to finish
	 */
	@SuppressWarnings("unused")
	public static long getAsyncTerminationWait()
	{
		return GuiceContext.asyncTerminationWait;
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
		return GuiceContext.asyncTerminationTimeUnit;
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
	 * Builds a reflection object if one does not exist
	 *
	 * @return A facade of the ReflectUtils on the scan result
	 */
	public static Reflections reflect()
	{
		if (GuiceContext.instance().reflections == null)
		{
			GuiceContext.instance().reflections = new Reflections();
		}
		return GuiceContext.instance().reflections;
	}

	/**
	 * Returns the actual context instance, provides access to methods existing a bit deeper
	 *
	 * @return The singleton instance of this
	 */
	public static GuiceContext instance()
	{
		return GuiceContext.instance;
	}

	/**
	 * Execute on Destroy
	 */
	@SuppressWarnings("unused")
	public static void destroy()
	{
		Set<IGuicePreDestroy> destroyers = GuiceContext.instance()
		                                               .getLoader(IGuicePreDestroy.class, ServiceLoader.load(IGuicePreDestroy.class));
		for (IGuicePreDestroy destroyer : destroyers)
		{
			IGuicePreDestroy instance = GuiceContext.get(destroyer.getClass());
			instance.onDestroy();
		}
		GuiceContext.instance().reflections = null;
		GuiceContext.instance().scanResult = null;
		GuiceContext.instance().scanner = null;
		GuiceContext.instance().injector = null;
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
		ExecutorService postLoaderExecutionService = Executors.newWorkStealingPool(GuiceContext.threadCount);
		for (IGuicePostStartup IGuicePostStartup : st)
		{
			runnables.add(new PostStartupRunnable(IGuicePostStartup));
		}
		for (PostStartupRunnable a : runnables)
		{
			try
			{
				postLoaderExecutionService.execute(a);
			}
			catch (Exception e)
			{
				GuiceContext.log.log(Level.SEVERE, "Unable to invoke Post Startups\n", e);
			}
		}
		postLoaderExecutionService.shutdown();
		try
		{
			postLoaderExecutionService.awaitTermination(1L, TimeUnit.SECONDS);
		}
		catch (Exception e)
		{
			GuiceContext.log.log(Level.SEVERE, "Could not execute asynchronous post loads", e);
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
	 * Method getAllLoadedServices returns the allLoadedServices of this GuiceContext object.
	 * <p>
	 * A list of all the loaded singleton sets
	 *
	 * @return the allLoadedServices (type Map Class, Set ) of this GuiceContext object.
	 */
	@NotNull
	public static Map<Class, Set> getAllLoadedServices()
	{
		return allLoadedServices;
	}

	/**
	 * Method loadPreStartups gets the pre startups and loads them up
	 */
	private void loadPreStartups()
	{
		Set<IGuicePreStartup> preStartups = getLoader(IGuicePreStartup.class, true, ServiceLoader.load(IGuicePreStartup.class));
		List<IGuicePreStartup> startups = new ArrayList<>();
		for (IGuicePreStartup preStartup : preStartups)
		{
			startups.add(preStartup);
		}
		startups.sort(Comparator.comparing(IGuicePreStartup::sortOrder));
		for (IGuicePreStartup startup : startups)
		{
			GuiceContext.log.config("Loading IGuicePreStartup - " +
			                        startup.getClass()
			                               .getCanonicalName());
			startup.onStartup();
		}
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

	private void loadConfiguration()
	{
		Set<IGuiceConfigurator> guiceConfigurators = getLoader(IGuiceConfigurator.class, true, ServiceLoader.load(IGuiceConfigurator.class));
		if (GuiceContext.config == null)
		{
			GuiceContext.config = new GuiceConfig<>();
		}
		for (IGuiceConfigurator guiceConfigurator : guiceConfigurators)
		{
			GuiceContext.log.config("Loading IGuiceConfigurator - " +
			                        guiceConfigurator.getClass()
			                                         .getCanonicalName());
			guiceConfigurator.configure(GuiceContext.config);
		}
		GuiceContext.log.config("IGuiceConfigurator  : " + GuiceContext.config.toString());
	}

	/**
	 * Starts up Guice and the scanner
	 */
	private void loadScanner()
	{
		Stopwatch stopwatch = Stopwatch.createStarted();
		GuiceContext.log.info("Loading Classpath Scanner - [" + GuiceContext.getThreadCount() + "] threads");
		if (GuiceContext.config == null)
		{
			loadConfiguration();
		}
		scanner = new ClassGraph();
		if (GuiceContext.config.isWhiteList())
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

			if (GuiceContext.config.isExcludeModulesAndJars())
			{
				String[] blacklistList = getPathsBlacklistList();
				if (blacklistList.length != 0)
				{
					scanner.blacklistPaths(blacklistList);
					scanner.blacklistPaths("META-INF/MANIFEST.MF");
				}
				String[] jarBlacklist = getJarsBlacklistList();
				if (jarBlacklist.length != 0)
				{
					scanner.blacklistJars(jarBlacklist);
				}
				String[] modulesBlacklist = getModulesBlacklistList();
				if (modulesBlacklist.length != 0)
				{
					scanner.blacklistModules(modulesBlacklist);
				}
			}
		}
		if (GuiceContext.config.isFieldInfo())
		{
			scanner.enableFieldInfo();
		}
		if (GuiceContext.config.isAnnotationScanning())
		{
			scanner.enableAnnotationInfo();
		}
		if (GuiceContext.config.isMethodInfo())
		{
			scanner.enableMethodInfo();
		}
		if (GuiceContext.config.isIgnoreFieldVisibility())
		{
			scanner.ignoreFieldVisibility();
		}
		if (GuiceContext.config.isIgnoreMethodVisibility())
		{
			scanner.ignoreMethodVisibility();
		}

		if (GuiceContext.config.isVerbose())
		{
			scanner.verbose();
		}
		try
		{
			scanResult = scanner.scan(GuiceContext.getThreadCount());
			Map<String, ResourceList.ByteArrayConsumer> fileScans = quickScanFiles();
			fileScans.forEach((key, value) ->
					                  scanResult.getResourcesWithLeafName(key)
					                            .forEachByteArray(value));
		}
		catch (Exception mpe)
		{
			GuiceContext.log.log(Level.SEVERE, "Unable to run scanner", mpe);
		}

		stopwatch.stop();
		GuiceContext.log.fine("Loaded Classpath Scanner -Took [" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "] millis.");
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPackagesList()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IPackageContentsScanner> exclusions = getLoader(IPackageContentsScanner.class, true, ServiceLoader.load(IPackageContentsScanner.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPackageContentsScanner exclusion : exclusions)
			{
				GuiceContext.log.log(Level.CONFIG, "Loading IPackageContentsScanner - " +
				                                   exclusion.getClass()
				                                            .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IPackageScanningContentsScanner - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * A set
	 *
	 * @param loaderType
	 * 		The service type
	 * @param <T>
	 * 		The type
	 * @param dontInject
	 * 		Don't inject
	 *
	 * @return A set of them
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> Set<T> getLoader(Class<T> loaderType, boolean dontInject, ServiceLoader<T> serviceLoader)
	{
		if (!getAllLoadedServices().containsKey(loaderType))
		{
			Set<T> loader = loaderToSetNoInjection(serviceLoader);
			getAllLoadedServices().put(loaderType, loader);
		}
		return getAllLoadedServices().get(loaderType);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPathsList()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IPathContentsScanner> exclusions = getLoader(IPathContentsScanner.class, true, ServiceLoader.load(IPathContentsScanner.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPathContentsScanner exclusion : exclusions)
			{
				GuiceContext.log.log(Level.CONFIG, "Loading IPathScanningContentsScanner - " +
				                                   exclusion.getClass()
				                                            .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IPathScanningContentsScanner - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPathsBlacklistList()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IPathContentsBlacklistScanner> exclusions = getLoader(IPathContentsBlacklistScanner.class, true, ServiceLoader.load(IPathContentsBlacklistScanner.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPathContentsBlacklistScanner exclusion : exclusions)
			{
				GuiceContext.log.log(Level.CONFIG, "Loading IPathContentsBlacklistScanner - " +
				                                   exclusion.getClass()
				                                            .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IPathContentsBlacklistScanner - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getJarsBlacklistList()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IGuiceScanJarExclusions> exclusions = getLoader(IGuiceScanJarExclusions.class, true, ServiceLoader.load(IGuiceScanJarExclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanJarExclusions exclusion : exclusions)
			{
				Set<String> searches = exclusion.excludeJars();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanJarExclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getModulesBlacklistList()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IGuiceScanModuleExclusions> exclusions = getLoader(IGuiceScanModuleExclusions.class, true, ServiceLoader.load(IGuiceScanModuleExclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanModuleExclusions exclusion : exclusions)
			{
				Set<String> searches = exclusion.excludeModules();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanModuleExclusions - " + strings.toString());
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
		Set<IFileContentsScanner> fileScanners = getLoader(IFileContentsScanner.class, true, ServiceLoader.load(IFileContentsScanner.class));
		for (IFileContentsScanner fileScanner : fileScanners)
		{
			GuiceContext.log.log(Level.CONFIG, "Loading IFileContentsScanner - " +
			                                   fileScanner.getClass()
			                                              .getCanonicalName());
			fileScans.putAll(fileScanner.onMatch());
		}
		return fileScans;
	}

	/**
	 * A set
	 *
	 * @param loaderType
	 * 		The service type
	 * @param <T>
	 * 		The type
	 *
	 * @return A set of them
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> Set<T> getLoader(Class<T> loaderType, ServiceLoader<T> serviceLoader)
	{
		if (!getAllLoadedServices().containsKey(loaderType))
		{
			Set<T> loader = null;
			if (GuiceContext.buildingInjector)
			{
				loader = loaderToSetNoInjection(serviceLoader);
			}
			else
			{
				loader = loaderToSet(serviceLoader);
			}
			getAllLoadedServices().put(loaderType, loader);
		}
		return getAllLoadedServices().get(loaderType);
	}

	@SuppressWarnings("unchecked")
	private List loadDefaultBinders()
	{
		Set<IGuiceModule> preStartups = getLoader(IGuiceModule.class, true, ServiceLoader.load(IGuiceModule.class));
		Set<IGuiceModule> startups = new TreeSet<>();
		startups.addAll(preStartups);
		List output = new ArrayList<>();
		for (IGuiceModule startup : startups)
		{
			GuiceContext.log.config("Loading IGuiceModule  - " +
			                        startup.getClass()
			                               .getCanonicalName());
			output.add(startup);
		}
		return output;
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
		GuiceContext.instance().scanner = scanner;
	}

	private void loadPostStartups()
	{
		Set<IGuicePostStartup> startupSet = getLoader(IGuicePostStartup.class, ServiceLoader.load(IGuicePostStartup.class));
		Map<Integer, Set<IGuicePostStartup>> postStartupGroups = new TreeMap<>();
		for (IGuicePostStartup postStartup : startupSet)
		{
			Integer sortOrder = postStartup.sortOrder();
			postStartupGroups.computeIfAbsent(sortOrder, k -> new TreeSet<>())
			                 .add(postStartup);
		}

		postStartupGroups.forEach((key, value) ->
		                          {
			                          if (value.size() == 1)
			                          {
				                          IGuicePostStartup postStartup = value.iterator()
				                                                               .next();
				                          GuiceContext.log.config("Loading IGuicePostStartup - " +
				                                                  postStartup
						                                                  .getClass()
						                                                  .getCanonicalName());
				                          postStartup.postLoad();
			                          }
			                          else
			                          {
				                          List<PostStartupRunnable> runnables = new ArrayList<>();
				                          GuiceContext.configureWorkStealingPool(new ArrayList<>(value), runnables);
			                          }
		                          });
		allLoadedServices.put(IGuiceModule.class, startupSet);
	}

	/**
	 * Returns the Guice Config Instance
	 *
	 * @return The singleton Guice Config instance. Also available with @Inject
	 */
	public GuiceConfig<?> getConfig()
	{
		return GuiceContext.config;
	}

}
