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
import com.google.inject.*;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.annotations.GuiceInjectorModuleMarker;
import com.jwebmp.guicedinjection.annotations.GuicePostStartup;
import com.jwebmp.guicedinjection.annotations.GuicePreStartup;
import com.jwebmp.guicedinjection.interfaces.GuiceConfigurator;
import com.jwebmp.guicedinjection.scanners.FileContentsScanner;
import com.jwebmp.guicedinjection.scanners.PackageContentsScanner;
import com.jwebmp.logger.LogFactory;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Modifier;
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
	/**
	 * If the references are built or not
	 */
	private static boolean built = false;
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
	private transient FastClasspathScanner scanner;
	/**
	 * The scan result built from everything - the core scanner.
	 */
	private transient ScanResult scanResult;
	/**
	 * Facade layer for backwards compatibility
	 */
	private transient Reflections reflections;
	/**
	 * A list of jars to exclude from the scan file for the application
	 */
	private Set<String> excludeJarsFromScan;

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
					"The injector is being called recursively during build. Place such actions in a GuicePostStartup or use the GuicePreStartup Service Loader.");
		}
		if (instance().injector == null)
		{
			try
			{
				buildingInjector = true;
				log.info("Starting up Injections");
				log.config("Pre Startup Executions....");
				Set<Class<? extends GuicePreStartup>> pres = reflect().getSubTypesOf(GuicePreStartup.class);
				pres.removeIf(a -> Modifier.isAbstract(a.getModifiers()));
				List<GuicePreStartup> startups = new ArrayList<>();
				for (Class<? extends GuicePreStartup> pre : pres)
				{
					GuicePreStartup pr;
					pr = pre.getDeclaredConstructor()
					        .newInstance();
					startups.add(pr);
				}
				startups.sort(Comparator.comparing(GuicePreStartup::sortOrder));
				log.log(Level.FINE, "Total of [{0}] startup modules.", startups.size());
				startups.forEach(GuicePreStartup::onStartup);
				log.config("Finished Startup Execution");

				log.info("Loading All Default Binders (that extend GuiceDefaultBinder)");

				GuiceInjectorModule defaultInjection;
				defaultInjection = new GuiceInjectorModule();
				log.info("Loading All Site Binders (that extend GuiceSiteBinder)");
				Set<Class<?>> aClass = reflect().getTypesAnnotatedWith(GuiceInjectorModuleMarker.class);
				aClass.removeIf(a -> Modifier.isAbstract(a.getModifiers()));
				int customModuleSize = aClass.size();
				log.log(Level.CONFIG, "Loading [{0}] Custom Modules", customModuleSize);
				ArrayList<Module> customModules = new ArrayList<>();
				Module[] cModules;
				for (Class<?> clazz : aClass)
				{
					Class<? extends AbstractModule> next = (Class<? extends AbstractModule>) clazz;
					log.log(Level.CONFIG, "Adding Module [{0}]", next.getCanonicalName());
					Module moduleInstance = next.getDeclaredConstructor()
					                            .newInstance();
					customModules.add(moduleInstance);
				}
				customModules.add(0, defaultInjection);

				cModules = new Module[customModules.size()];
				cModules = customModules.toArray(cModules);

				instance().injector = Guice.createInjector(cModules);
				log.info("Post Startup Executions....");
				Set<Class<? extends GuicePostStartup>> closingPres = reflect().getSubTypesOf(GuicePostStartup.class);
				closingPres.removeIf(a -> Modifier.isAbstract(a.getModifiers()));
				List<GuicePostStartup> postStartups = new ArrayList<>();
				Map<Integer, List<GuicePostStartup>> postStartupGroups = new TreeMap<>();
				buildingInjector = false;
				closingPres.forEach(closingPre -> postStartups.add(GuiceContext.getInstance(closingPre)));
				postStartups.sort(Comparator.comparing(GuicePostStartup::sortOrder));
				log.log(Level.CONFIG, "Total of [{0}] startup modules.", postStartups.size());
				postStartups.forEach(a ->
				                     {
					                     Integer sortOrder = a.sortOrder();
					                     postStartupGroups.computeIfAbsent(sortOrder, k -> new ArrayList<>())
					                                      .add(a);
				                     });
				postStartupGroups.forEach((key, value) ->
				                          {
					                          List<GuicePostStartup> st = postStartupGroups.get(key);
					                          List<PostStartupRunnable> runnables = new ArrayList<>();
					                          if (st.size() == 1)
					                          {
						                          st.get(0)
						                            .postLoad();
					                          }
					                          else
					                          {
						                          configureWorkStealingPool(st, runnables);
					                          }
				                          });
				log.fine("Finished Post Startup Execution");
				log.config("Injection System Ready");
			}
			catch (Throwable e)
			{
				log.log(Level.SEVERE, "Exception creating Injector : " + e.getMessage(), e);
			}
			built = true;
		}
		buildingInjector = false;
		return instance().injector;
	}

	/**
	 * Builds an asynchronous running pool to execute with a termination waiter
	 *
	 * @param st
	 * 		A list of startup objects
	 * @param runnables
	 * 		A list of post startup threads
	 */
	private static void configureWorkStealingPool(List<GuicePostStartup> st, List<PostStartupRunnable> runnables)
	{
		ExecutorService postLoaderExecutionService = Executors.newWorkStealingPool(threadCount);
		for (GuicePostStartup guicePostStartup : st)
		{
			runnables.add(new PostStartupRunnable(guicePostStartup));
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
	 * If context can be used
	 *
	 * @return boolean denoting if the injector is ready to be called
	 */
	public static boolean isReady()
	{
		return isBuilt() && isBuildingInjector();
	}

	/**
	 * If the context is built
	 *
	 * @return denoting if the injector is ready to be called
	 */
	public static boolean isBuilt()
	{
		return built;
	}

	/**
	 * If the context is built
	 *
	 * @param built
	 * 		denoting if the injector is ready to be called
	 */
	public static void setBuilt(boolean built)
	{
		GuiceContext.built = built;
	}

	/**
	 * If the context is currently still building the injector
	 *
	 * @return denoting if the injector is ready to be called
	 */
	public static boolean isBuildingInjector()
	{
		return buildingInjector;
	}

	/**
	 * Returns the assigned logger for changing the level of output or adding handlers
	 *
	 * @return This classes physical logger
	 */
	public static Logger getLog()
	{
		return log;
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

	/**
	 * Starts up Guice and the scanner
	 */
	public void loadScanner()
	{
		log.info("Starting up classpath scanner");
		Stopwatch stopwatch = Stopwatch.createStarted();
		log.fine("Loading the Guice Config.");

		ServiceLoader<GuiceConfigurator> guiceConfigurators = ServiceLoader.load(GuiceConfigurator.class);
		if (config == null)
		{
			config = new GuiceConfig<>();
		}
		for (GuiceConfigurator guiceConfigurator : guiceConfigurators)
		{
			config = guiceConfigurator.configure(config);
		}

		log.config("Using Configuration : " + config.toString());

		if (config.isWhiteList())
		{
			scanner = new FastClasspathScanner(getPackagesList());
		}
		else
		{
			scanner = new FastClasspathScanner();
			log.warning(
					"Scanning may be slow because white listing is disabled. If you experience long scan times, you can configure using META-INF/services/za.co.mmagon.guiceinjection.interfaces.GuiceConfigurator. White List the packages to be scanned with META-INF/services/com.jwebmp.guiceinjection.scanners.PackageContentsScanner");
		}
		if (config.isFieldInfo())
		{
			scanner.enableFieldInfo();
		}
		if (config.isFieldAnnotationScanning())
		{
			scanner.enableFieldAnnotationIndexing();
		}
		if (config.isMethodAnnotationIndexing())
		{
			scanner.enableMethodAnnotationIndexing();
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
		registerScanQuickFiles(scanner);
		scanResult = scanner.scan(getThreadCount());
		stopwatch.stop();
		log.info("Classpath Scanner Completed with [" + getThreadCount() + "] threads. Took [" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "] millis.");
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	private String[] getPackagesList()
	{
		log.fine("Starting scan for package monitors. Services registered with " + PackageContentsScanner.class.getCanonicalName() + " will be found.");
		if (excludeJarsFromScan == null || excludeJarsFromScan.isEmpty())
		{
			excludeJarsFromScan = new HashSet<>();
			ServiceLoader<PackageContentsScanner> exclusions = ServiceLoader.load(PackageContentsScanner.class);
			for (PackageContentsScanner exclusion : exclusions)
			{
				Set<String> searches = exclusion.searchFor();
				log.log(Level.CONFIG, "Added to Scanned Packages : " + searches);
				excludeJarsFromScan.addAll(searches);
			}
		}
		String[] exclusions = new String[excludeJarsFromScan.size()];
		log.config("Package Monitoring complete. Total Packages registered for scan [" + exclusions.length + "].");
		return excludeJarsFromScan.toArray(exclusions);
	}

	/**
	 * Registers the quick scan files
	 *
	 * @param scanner
	 * 		The instance of Classpath Scanner to load with file matching
	 */
	@SuppressWarnings("unchecked")
	private void registerScanQuickFiles(FastClasspathScanner scanner)
	{
		log.fine("Starting File Contents Scanner. Services registered with " + FileContentsScanner.class.getCanonicalName() + " will be found.");
		ServiceLoader<FileContentsScanner> fileScanners = ServiceLoader.load(FileContentsScanner.class);
		int found = 0;
		for (FileContentsScanner fileScanner : fileScanners)
		{
			fileScanner.onMatch()
			           .forEach(scanner::matchFilenamePathLeaf);
			found++;
		}
		log.config("File Contents Scanner Matchers have been registered. Total Content Scanners [" + found + "].");
	}

	/**
	 * Gets the number of threads to use when processing
	 * Default processors count
	 *
	 * @return Default processors count
	 */
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
	public FastClasspathScanner getScanner()
	{
		return scanner;
	}

	/**
	 * Sets the classpath scanner
	 *
	 * @param scanner
	 * 		Sets the scanner to a specific instance
	 */
	public static void setScanner(FastClasspathScanner scanner)
	{
		instance().scanner = scanner;
	}

	/**
	 * Maps the injector class to the injector
	 *
	 * @return The global Guice Injector
	 */
	public Injector getInjector()
	{
		return inject();
	}

	/**
	 * Sets the given injector to this context
	 *
	 * @param injector
	 * 		A specific Injector instance
	 */
	public void setInjector(Injector injector)
	{
		this.injector = injector;
	}

	/**
	 * Returns the fully populated reflections object
	 *
	 * @return A facade of the ReflectUtils on the scan result
	 */
	@SuppressWarnings("unused")
	public Reflections getReflections()
	{
		return reflect();
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
