/*
 * Copyright (C) 2017 GedMarc
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
import java.time.temporal.ChronoUnit;
import java.util.*;
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
	@SuppressWarnings("unchecked")
	private static final Map<Class, Set> allLoadedServices = Collections.synchronizedMap(new LinkedHashMap());
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
	private Injector injector;
	/**
	 * The actual scanner
	 */
	private ClassGraph scanner;
	/**
	 * The scan result built from everything - the core scanner.
	 */
	private ScanResult scanResult;
	/**
	 * Facade layer for backwards compatibility
	 */
	private Reflections reflections;
	/**
	 * Boolean dictating if a synchronized thread should be locked
	 */
	private boolean lockSynchronizedThreads;
	/**
	 * The time to wait for asynchronous post startup groups
	 */
	private long synchronousThreadTimeout = 1L;
	/**
	 * The unit of time to wait for timeout blocks
	 */
	private ChronoUnit synchronousThreadTimeoutUnit = ChronoUnit.NANOS;


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
				                .isPathScanning() ||
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
		if (GuiceContext.instance().scanResult != null)
		{
			GuiceContext.instance().scanResult.close();
		}
		GuiceContext.instance().reflections = null;
		if (GuiceContext.instance().scanResult != null)
		{
			GuiceContext.instance().scanResult.close();
		}
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
		postLoaderExecutionService.shutdownNow();
		try
		{
			GuiceContext.log.log(Level.CONFIG, "Waiting for Database Startups...");
			if (!instance().isLockSynchronizedThreads())
			{
				postLoaderExecutionService.awaitTermination(GuiceContext.getAsyncTerminationWait(), GuiceContext.getAsyncTerminationTimeUnit());
			}
			else
			{
				postLoaderExecutionService.awaitTermination(1L, TimeUnit.NANOSECONDS);
			}
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
	 * Returns the Java version as an int value.
	 *
	 * @return the Java version as an int value (8, 9, etc.)
	 *
	 * @since 12130
	 */
	public static int getJavaVersion()
	{
		String version = System.getProperty("java.version");
		if (version.startsWith("1."))
		{
			version = version.substring(2);
		}
		// Allow these formats:
		// 1.8.0_72-ea
		// 9-ea
		// 9
		// 9.0.1
		int dotPos = version.indexOf('.');
		int dashPos = version.indexOf('-');
		String value = version.substring(0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : version.length());
		return Integer.parseInt(value);
	}

	/**
	 * Method loadPreStartups gets the pre startups and loads them up
	 */
	private void loadPreStartups()
	{
		Set<IGuicePreStartup> preStartups = getLoader(IGuicePreStartup.class, true, ServiceLoader.load(IGuicePreStartup.class));
		List<IGuicePreStartup> startups = new ArrayList<>(preStartups);
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

	/**
	 * Loads the IGuiceConfigurator
	 */
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
		configureScanner(scanner);
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
		GuiceContext.log.fine("Loaded Classpath Scanner - Took [" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "] millis.");
	}

	/**
	 * Configures the scanner from its setup
	 *
	 * @param graph
	 * 		The ClassGraph to apply the configuration to
	 */
	private void configureScanner(ClassGraph graph)
	{
		if (config.isWhitelistPaths())
		{
			String[] paths = getPathsList();
			if (paths.length != 0)
			{
				graph.whitelistPathsNonRecursive(paths);
			}
		}
		if (GuiceContext.config.isExcludePaths())
		{
			String[] blacklistList = getPathsBlacklistList();
			if (blacklistList.length != 0)
			{
				graph.blacklistPaths(blacklistList);
			}
		}

		if (config.isWhitelistJarsAndModules())
		{
			if (getJavaVersion() < 9)
			{
				String[] jarBlacklist = getJarsWhiteList();
				if (jarBlacklist.length != 0)
				{
					graph.blacklistJars(jarBlacklist);
				}
			}
			else
			{
				String[] modulesBlacklist = getModulesWhiteList();
				if (modulesBlacklist.length != 0)
				{
					graph.whitelistModules(modulesBlacklist);
				}
			}
		}
		if (GuiceContext.config.isExcludeModulesAndJars())
		{
			if (getJavaVersion() < 9)
			{
				String[] jarBlacklist = getJarsBlacklistList();
				if (jarBlacklist.length != 0)
				{
					graph.blacklistJars(jarBlacklist);
				}
			}
			else
			{
				String[] modulesBlacklist = getModulesBlacklistList();
				if (modulesBlacklist.length != 0)
				{
					graph.blacklistModules(modulesBlacklist);
				}
				else
				{
					graph.ignoreParentModuleLayers();
				}
			}
		}

		if (GuiceContext.config.isWhiteListPackages())
		{
			String[] packages = getPackagesList();
			if (packages.length != 0)
			{
				graph.whitelistPackages(packages);
			}
		}
		if (GuiceContext.config.isBlackListPackages())
		{
			String[] packages = getBlacklistPackages();
			if (packages.length != 0)
			{
				graph.blacklistPackages(packages);
			}
		}

		if (GuiceContext.config.isExcludeParentModules())
		{
			graph.ignoreParentModuleLayers();
		}

		if (GuiceContext.config.isFieldInfo())
		{
			graph.enableFieldInfo();
		}

		if (GuiceContext.config.isAnnotationScanning())
		{
			graph.enableAnnotationInfo();
		}
		if (GuiceContext.config.isMethodInfo())
		{
			graph.enableMethodInfo();
		}
		if (GuiceContext.config.isIgnoreFieldVisibility())
		{
			graph.ignoreFieldVisibility();
		}
		if (GuiceContext.config.isIgnoreMethodVisibility())
		{
			graph.ignoreMethodVisibility();
		}

		if (GuiceContext.config.isVerbose())
		{
			graph.verbose();
		}
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
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getBlacklistPackages()
	{
		Set<String> strings = new LinkedHashSet<>();
		Set<IPackageBlackListScanner> exclusions = getLoader(IPackageBlackListScanner.class, true, ServiceLoader.load(IPackageBlackListScanner.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPackageBlackListScanner exclusion : exclusions)
			{
				GuiceContext.log.log(Level.CONFIG, "Loading IPackageContentsScanner - " +
				                                   exclusion.getClass()
				                                            .getCanonicalName());
				Set<String> searches = exclusion.exclude();
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
	public <T> Set<T> getLoader(Class<T> loaderType, @SuppressWarnings("unused") boolean dontInject, ServiceLoader<T> serviceLoader)
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
		Set<String> strings = new TreeSet<>();
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
		Set<String> strings = new TreeSet<>();
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
	@SuppressWarnings("unchecked")
	private String[] getJarsWhiteList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanJarInclusions> exclusions = getLoader(IGuiceScanJarInclusions.class, true, ServiceLoader.load(IGuiceScanJarInclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanJarInclusions exclusion : exclusions)
			{
				Set<String> searches = exclusion.includeJars();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanJarInclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	@SuppressWarnings("unchecked")
	private String[] getModulesWhiteList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanModuleInclusions> exclusions = getLoader(IGuiceScanModuleInclusions.class, true, ServiceLoader.load(IGuiceScanModuleInclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanModuleInclusions exclusion : exclusions)
			{
				Set<String> searches = exclusion.includeModules();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanModuleInclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	@SuppressWarnings("unchecked")
	private String[] getJarsBlacklistList()
	{
		Set<String> strings = new TreeSet<>();
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
	@SuppressWarnings("unchecked")
	private String[] getModulesBlacklistList()
	{
		Set<String> strings = new TreeSet<>();
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
	 * Method loadDefaultBinders ...
	 *
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	private List loadDefaultBinders()
	{
		Set<IGuiceModule> preStartups = getLoader(IGuiceModule.class, true, ServiceLoader.load(IGuiceModule.class));
		Set<IGuiceModule> startups = new TreeSet<>(preStartups);
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

	/**
	 * Method loadPostStartups ...
	 */
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

	/**
	 * If the post startup asynchronous threads should for (x) amount of time
	 *
	 * @return if active
	 */
	public boolean isLockSynchronizedThreads()
	{
		return lockSynchronizedThreads;
	}

	public GuiceContext setLockSynchronizedThreads(boolean lockSynchronizedThreads)
	{
		this.lockSynchronizedThreads = lockSynchronizedThreads;
		return this;
	}

	/**
	 * If the post startup asynchronous threads should for (x) amount of time
	 *
	 * @param lockSynchronizedThreads
	 * 		if locked
	 *
	 * @return this instance
	 */
	public GuiceContext setLockSynchronizedThreads(boolean lockSynchronizedThreads, Long time, ChronoUnit unit)
	{
		this.lockSynchronizedThreads = lockSynchronizedThreads;
		this.setSynchronousThreadTimeout(time);
		this.setSynchronousThreadTimeoutUnit(unit);
		return this;
	}

	/**
	 * Method getSynchronousThreadTimeout returns the synchronousThreadTimeout of this GuiceContext object.
	 * <p>
	 * The time to wait for asynchronous post startup groups
	 *
	 * @return the synchronousThreadTimeout (type long) of this GuiceContext object.
	 */
	public long getSynchronousThreadTimeout()
	{
		return synchronousThreadTimeout;
	}

	public GuiceContext setSynchronousThreadTimeout(long synchronousThreadTimeout)
	{
		this.synchronousThreadTimeout = synchronousThreadTimeout;
		return this;
	}

	/**
	 * Method getSynchronousThreadTimeoutUnit returns the synchronousThreadTimeoutUnit of this GuiceContext object.
	 * <p>
	 * The unit of time to wait for timeout blocks
	 *
	 * @return the synchronousThreadTimeoutUnit (type ChronoUnit) of this GuiceContext object.
	 */
	public ChronoUnit getSynchronousThreadTimeoutUnit()
	{
		return synchronousThreadTimeoutUnit;
	}

	/**
	 * Method setSynchronousThreadTimeoutUnit sets the synchronousThreadTimeoutUnit of this GuiceContext object.
	 * <p>
	 * The unit of time to wait for timeout blocks
	 *
	 * @param synchronousThreadTimeoutUnit
	 * 		the synchronousThreadTimeoutUnit of this GuiceContext object.
	 *
	 * @return GuiceContext
	 */
	public GuiceContext setSynchronousThreadTimeoutUnit(ChronoUnit synchronousThreadTimeoutUnit)
	{
		this.synchronousThreadTimeoutUnit = synchronousThreadTimeoutUnit;
		return this;
	}

	/**
	 * Loads the service lists of post startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IGuicePostStartup> loadPostStartupServices()
	{
		return getLoader(IGuicePostStartup.class, ServiceLoader.load(IGuicePostStartup.class));
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
			Set<T> loader;
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
}
