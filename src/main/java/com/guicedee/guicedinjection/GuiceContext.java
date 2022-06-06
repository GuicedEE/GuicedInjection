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
package com.guicedee.guicedinjection;

import com.google.common.base.*;
import com.google.inject.*;
import com.guicedee.guicedinjection.abstractions.*;
import com.guicedee.guicedinjection.interfaces.*;
import com.guicedee.guicedinjection.interfaces.annotations.*;
import com.guicedee.logger.*;
import io.github.classgraph.*;
import jakarta.validation.constraints.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.regex.Pattern;

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
public class GuiceContext<J extends GuiceContext<J>>
{
	/**
	 * Field log
	 */
	private static final Logger log = LogFactory.getLog("GuiceContext");
	/**
	 * This particular instance of the class
	 */
	private static final GuiceContext<?> instance = new GuiceContext<>();
	/**
	 * A list of all the loaded singleton sets
	 */
	private static final Map<Class, Set> allLoadedServices = Collections.synchronizedMap(new LinkedHashMap<>());
	/**
	 * The building injector
	 */
	public static boolean buildingInjector = false;
	/**
	 * A standard default waiting time for threads
	 */
	public static long defaultWaitTime = 2;
	/**
	 * The default wait unit for the thread time
	 */
	public static TimeUnit defaultWaitUnit = TimeUnit.SECONDS;
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
	 * If the scan should run async
	 */
	private boolean async;
	
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
	public static synchronized Injector inject()
	{
		if (GuiceContext.buildingInjector)
		{
			log.log(Level.SEVERE, "The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.");
			System.exit(1);
		}
		if (GuiceContext.instance().injector == null)
		{
			try
			{
				GuiceContext.buildingInjector = true;
				GuiceContext.log.info("Starting up Guice Context");
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
				GuiceContext.instance()
				            .loadPreStartups();
				
				List<com.google.inject.Module> cModules = new ArrayList<>();
				cModules.add(new GuiceInjectorModule());
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
	 * @param <T>  The type to retrieve
	 * @param type The physical class object
	 * @return The scoped object
	 */
	@NotNull
	public static <T> T get(@NotNull Class<T> type)
	{
		return get(type, null);
	}
	
	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>  The type to retrieve
	 * @param type The physical class object
	 * @return The scoped object
	 * @deprecated For get()
	 */
	@NotNull
	@Deprecated()
	public static <T> T getInstance(@NotNull Class<T> type)
	{
		return get(type, null);
	}
	
	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>  The type to retrieve
	 * @param type The physical class object
	 * @return The scoped object
	 * @deprecated For get()
	 */
	@NotNull
	@Deprecated()
	public static <T> T getInstance(@NotNull Key<T> type)
	{
		return get(type);
	}
	
	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>  The type to retrieve
	 * @param type The physical class object
	 * @return The scoped object
	 * @deprecated For get()
	 */
	@NotNull
	@Deprecated()
	public static <T> T getInstance(@NotNull Class<T> type, Class<? extends Annotation> annotation)
	{
		return get(type, annotation);
	}
	
	private static boolean isEntityType(Class<?> clazz)
	{
		try
		{
			for (Annotation annotation : clazz.getAnnotations())
			{
				if (annotation.annotationType()
				              .getCanonicalName()
				              .equalsIgnoreCase("jakarta.persistence.Entity"))
				{
					return true;
				}
			}
		}catch (NullPointerException npe )
		{
			return false;
		}
		return false;
	}
	
	private static boolean isNotEnhanceable(Class<?> clazz)
	{
		return clazz.isAnnotationPresent(INotEnhanceable.class);
	}
	
	private static boolean isNotInjectable(Class<?> clazz)
	{
		return clazz.isAnnotationPresent(INotInjectable.class);
	}
	
	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>        The type to retrieve
	 * @param type       The physical class object
	 * @param annotation The annotation to fetch
	 * @return The scoped object
	 */
	public static <T> T get(@NotNull Class<T> type, Class<? extends Annotation> annotation)
	{
		if (annotation == null)
		{
			return get(Key.get(type));
		}
		return get(Key.get(type, annotation));
	}
	
	/**
	 * Gets a new specified instance from a give key
	 *
	 * @param <T>  The type to retrieve
	 * @param type The physical class object
	 * @return The scoped object
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> T get(@NotNull Key<T> type)
	{
		Class<T> clazz = (Class<T>) type.getTypeLiteral()
		                                .getRawType();
		T instance;
		boolean isEntityType = isEntityType(clazz);
		if (isNotEnhanceable(clazz) || isEntityType)
		{
			try
			{
				instance = clazz.getDeclaredConstructor()
				                .newInstance();
				if (!isNotInjectable(clazz))
				{
					inject().injectMembers(instance);
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Unable to construct [" + clazz.getCanonicalName() + "]. Not Enhanceable or an Entity.", e);
				throw new RuntimeException(e);
			}
		}
		else
		{
			instance = inject().getInstance(type);
		}
		return instance;
	}
	
	/**
	 * Execute on Destroy
	 */
	@SuppressWarnings("unused")
	public static void destroy()
	{
		Set<IGuicePreDestroy> destroyers = GuiceContext.instance()
		                                               .getLoader(IGuicePreDestroy.class, true, ServiceLoader.load(IGuicePreDestroy.class));
		for (IGuicePreDestroy destroyer : destroyers)
		{
			IGuicePreDestroy instance = GuiceContext.get(destroyer.getClass());
			instance.onDestroy();
		}
		if (GuiceContext.instance().scanResult != null)
		{
			GuiceContext.instance().scanResult.close();
		}
		if (GuiceContext.instance().scanResult != null)
		{
			GuiceContext.instance().scanResult.close();
		}
		GuiceContext.instance().scanResult = null;
		GuiceContext.instance().scanner = null;
		GuiceContext.instance().injector = null;
	}
	
	/**
	 * Returns the Java version as an int value.
	 *
	 * @return the Java version as an int value (8, 9, etc.)
	 * @since 12130
	 */
	private static int getJavaVersion()
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
	 * @param scanResult The physical Scan Result from the complete class scanner
	 */
	@SuppressWarnings("unused")
	public void setScanResult(ScanResult scanResult)
	{
		GuiceContext.instance().scanResult = scanResult;
	}
	
	/**
	 * Returns the actual context instance, provides access to methods existing a bit deeper
	 *
	 * @return The singleton instance of this
	 */
	public static GuiceContext<?> instance()
	{
		return GuiceContext.instance;
	}
	
	private static boolean configured;
	
	/**
	 * Loads the IGuiceConfigurator
	 */
	private void loadConfiguration()
	{
		if (!configured)
		{
			if (GuiceContext.config == null)
			{
				GuiceContext.config = new GuiceConfig<>();
			}
			Set<IGuiceConfigurator> guiceConfigurators = loadIGuiceConfigs();
			for (IGuiceConfigurator guiceConfigurator : guiceConfigurators)
			{
				GuiceContext.log.config("Loading IGuiceConfigurator - " +
				                        guiceConfigurator.getClass()
				                                         .getCanonicalName());
				GuiceContext.config = guiceConfigurator.configure(GuiceContext.config);
			}
			GuiceContext.log.config("IGuiceConfigurator  : " + GuiceContext.config.toString());
			configured = true;
		}
	}
	
	
	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	@SuppressWarnings("unchecked")
	private String[] getJarsExclusionList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanJarExclusions> exclusions = loadJarRejectScanners();
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
	private String[] getJarsInclusionList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanJarInclusions> exclusions = loadJarInclusionScanners();
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanJarInclusions exclusion : exclusions)
			{
				Set<String> searches = exclusion.includeJars();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanJarExclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}
	
	
	/**
	 * Starts up Guice and the scanner
	 */
	private synchronized void loadScanner()
	{
		if (scanner == null)
		{
			scanner = new ClassGraph();
			Stopwatch stopwatch = Stopwatch.createStarted();
			GuiceContext.log.info("Loading Classpath Scanner");
			loadConfiguration();
			scanner = configureScanner(scanner);
			try
			{
				if (async)
				{
					scanResult = scanner.scan(Runtime.getRuntime()
					                                 .availableProcessors());
				}
				else
				{
					scanResult = scanner.scan();
				}
				stopwatch.stop();
				Map<String, ResourceList.ByteArrayConsumer> fileScans = quickScanFiles();
				fileScans.forEach((key, value) ->
						scanResult.getResourcesWithLeafName(key)
						          .forEachByteArrayIgnoringIOException(value));
				quickScanFilesPattern().forEach(
						(key, value) ->
								scanResult.getResourcesMatchingPattern(key)
								          .forEachByteArrayIgnoringIOException(value));
				
			}
			catch (Exception mpe)
			{
				GuiceContext.log.log(Level.SEVERE, "Unable to run scanner", mpe);
			}
			GuiceContext.log.fine("Loaded Classpath Scanner - Took [" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "] millis.");
		}
	}
	
	/**
	 * Configures the scanner from its setup
	 *
	 * @param graph The ClassGraph to apply the configuration to
	 */
	private ClassGraph configureScanner(ClassGraph graph)
	{
		if (config.isAllowPaths())
		{
			String[] paths = getPathsList();
			if (paths.length != 0)
			{
				graph = graph.acceptPaths(paths);
			}
		}
		if (GuiceContext.config.isExcludePaths())
		{
			String[] blacklistList = getPathsBlacklistList();
			if (blacklistList.length != 0)
			{
				graph = graph.rejectPaths(blacklistList);
			}
		}
		
		if (GuiceContext.config.isExcludeModulesAndJars())
		{
			if (getJavaVersion() < 9)
			{
				String[] jarRejections = getJarsExclusionList();
				if (jarRejections.length != 0)
				{
					graph = graph.rejectJars(jarRejections);
				}
			}
			else
			{
				String[] modulesRejection = getModulesExclusionList();
				if (modulesRejection.length != 0)
				{
					graph = graph.rejectModules(modulesRejection);
				}
				else
				{
					graph = graph.ignoreParentModuleLayers();
				}
			}
		}
		
		if (GuiceContext.config.isIncludeModuleAndJars())
		{
			if (getJavaVersion() < 9)
			{
				String[] jarRejections = getJarsInclusionList();
				log.config("Accepted Jars for Scanning : " + Arrays.toString(jarRejections));
				if (jarRejections.length != 0)
				{
					graph = graph.acceptJars(jarRejections);
				}
			}
			else
			{
				String[] modulesRejection = getModulesInclusionsList();
				log.config("Accepted Modules for Scanning : " + Arrays.toString(modulesRejection));
				if (modulesRejection.length != 0)
				{
					graph = graph.acceptModules(modulesRejection);
				}
				else
				{
					graph = graph.ignoreParentModuleLayers();
				}
			}
		}
		
		if (GuiceContext.config.isIncludePackages())
		{
			String[] packages = getPackagesList();
			if (packages.length != 0)
			{
				graph = graph.acceptPackages(packages);
			}
		}
		if (GuiceContext.config.isRejectPackages())
		{
			String[] packages = getBlacklistPackages();
			if (packages.length != 0)
			{
				graph = graph.rejectPackages(packages);
			}
		}
		if (GuiceContext.config.isExcludeParentModules())
		{
			graph = graph.ignoreParentModuleLayers();
		}
		if (GuiceContext.config.isFieldInfo())
		{
			graph = graph.enableClassInfo();
			graph = graph.enableFieldInfo();
		}
		if (GuiceContext.config.isAnnotationScanning())
		{
			graph = graph.enableClassInfo();
			graph = graph.enableAnnotationInfo();
		}
		if (GuiceContext.config.isMethodInfo())
		{
			graph = graph.enableClassInfo();
			graph = graph.enableMethodInfo();
		}
		if (GuiceContext.config.isIgnoreFieldVisibility())
		{
			graph = graph.enableClassInfo();
			graph = graph.ignoreFieldVisibility();
		}
		if (GuiceContext.config.isIgnoreMethodVisibility())
		{
			graph = graph.enableClassInfo();
			graph = graph.ignoreMethodVisibility();
		}
		if (GuiceContext.config.isClasspathScanning())
		{
			graph = graph.enableClassInfo();
		}
		if (GuiceContext.config.isVerbose())
		{
			graph = graph.verbose();
		}
		if (GuiceContext.config.isIgnoreClassVisibility())
		{
			graph = graph.ignoreClassVisibility();
		}
		return graph;
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
		Set<IPackageRejectListScanner> exclusions = getLoader(IPackageRejectListScanner.class, true, ServiceLoader.load(IPackageRejectListScanner.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPackageRejectListScanner exclusion : exclusions)
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
	
	/*
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
		Set<IPathContentsRejectListScanner> exclusions = loadPathRejectScanners();
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IPathContentsRejectListScanner exclusion : exclusions)
			{
				GuiceContext.log.log(Level.CONFIG, "Loading IPathContentsRejectListScanner - " +
				                                   exclusion.getClass()
				                                            .getCanonicalName());
				Set<String> searches = exclusion.searchFor();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IPathContentsRejectListScanner - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}
	
	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	@SuppressWarnings("unchecked")
	private String[] getModulesExclusionList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanModuleExclusions> exclusions = getLoader(IGuiceScanModuleExclusions.class, true, ServiceLoader.load(IGuiceScanModuleExclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanModuleExclusions<?> exclusion : exclusions)
			{
				Set<String> searches = exclusion.excludeModules();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanModuleExclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}
	
	
	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return A string list of packages to be scanned
	 */
	@SuppressWarnings("unchecked")
	private String[] getModulesInclusionsList()
	{
		Set<String> strings = new TreeSet<>();
		Set<IGuiceScanModuleInclusions> exclusions = getLoader(IGuiceScanModuleInclusions.class, true, ServiceLoader.load(IGuiceScanModuleInclusions.class));
		if (exclusions.iterator()
		              .hasNext())
		{
			for (IGuiceScanModuleInclusions<?> exclusion : exclusions)
			{
				Set<String> searches = exclusion.includeModules();
				strings.addAll(searches);
			}
			GuiceContext.log.log(Level.FINE, "IGuiceScanModuleInclusions - " + strings.toString());
		}
		return strings.toArray(new String[0]);
	}
	
	/**
	 * Registers the quick scan files
	 */
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
	 * Registers the quick scan files
	 */
	private Map<Pattern, ResourceList.ByteArrayConsumer> quickScanFilesPattern()
	{
		Map<Pattern, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
		Set<IFileContentsPatternScanner> fileScanners = getLoader(IFileContentsPatternScanner.class, true, ServiceLoader.load(IFileContentsPatternScanner.class));
		for (IFileContentsPatternScanner fileScanner : fileScanners)
		{
			GuiceContext.log.log(Level.CONFIG, "Loading IFileContentsPatternScanner - " +
			                                   fileScanner.getClass()
			                                              .getCanonicalName());
			fileScans.putAll(fileScanner.onMatch());
		}
		return fileScans;
	}
	
	/**
	 * A set
	 *
	 * @param loaderType The service type
	 * @param <T>        The type
	 * @param dontInject Don't inject
	 * @return A set of them
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> Set<T> getLoader(Class<T> loaderType,
	                            @SuppressWarnings("unused")
	                            boolean dontInject, ServiceLoader<T> serviceLoader)
	{
		if (!getAllLoadedServices().containsKey(loaderType))
		{
			Set<T> loader = IDefaultService.loaderToSetNoInjection(serviceLoader);
			getAllLoadedServices().put(loaderType, loader);
		}
		return getAllLoadedServices().get(loaderType);
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
	 * @param scanner Sets the scanner to a specific instance
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
		Set<IGuicePostStartup> startupSet = loadPostStartupServices();
		
		Map<Integer, Set<IGuicePostStartup>> postStartupGroups = new TreeMap<>();
		for (IGuicePostStartup<?> postStartup : startupSet)
		{
			Integer sortOrder = postStartup.sortOrder();
			postStartupGroups.computeIfAbsent(sortOrder, k -> new TreeSet<>())
			                 .add(postStartup);
		}
		
		for (Map.Entry<Integer, Set<IGuicePostStartup>> entry : postStartupGroups.entrySet())
		{
			Integer key = entry.getKey();
			Set<IGuicePostStartup> value = entry.getValue();
			if (value.size() == 1)
			{
				//run in order
				for (IGuicePostStartup<?> iGuicePostStartup : value)
				{
					try
					{
						iGuicePostStartup.postLoad();
					}
					catch (Throwable T)
					{
						log.log(Level.SEVERE, "Cannot execute post startup - " + iGuicePostStartup.getClass()
						                                                                          .getCanonicalName(), T);
					}
				}
			}
			else
			{
				try
				{
					value.parallelStream()
					     .forEach(IGuicePostStartup::postLoad);
				}
				catch (Throwable T)
				{
					log.log(Level.SEVERE, "Cannot execute post startup - ", T);
				}
			}
			GuiceContext.log.fine("Completed with Post Startups Key [" + key + "]");
		}
	}
	
	/**
	 * Returns the Guice Config Instance
	 *
	 * @return The singleton Guice Config instance. Also available with @Inject
	 */
	public GuiceConfig<?> getConfig()
	{
		if (GuiceContext.config == null)
		{
			GuiceContext.config = new GuiceConfig<>();
		}
		return GuiceContext.config;
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
	 * Loads the service lists of post startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IPathContentsRejectListScanner> loadPathRejectScanners()
	{
		return getLoader(IPathContentsRejectListScanner.class, true, ServiceLoader.load(IPathContentsRejectListScanner.class));
	}
	
	
	/**
	 * Loads the service lists of post startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IGuiceScanJarExclusions> loadJarRejectScanners()
	{
		return getLoader(IGuiceScanJarExclusions.class, true, ServiceLoader.load(IGuiceScanJarExclusions.class));
	}
	
	
	/**
	 * Loads the service lists of post startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IGuiceScanJarInclusions> loadJarInclusionScanners()
	{
		return getLoader(IGuiceScanJarInclusions.class, true, ServiceLoader.load(IGuiceScanJarInclusions.class));
	}
	
	
	/**
	 * Returns the set of service lists of pre startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IGuicePreStartup> loadPreStartupServices()
	{
		return getLoader(IGuicePreStartup.class, true, ServiceLoader.load(IGuicePreStartup.class));
	}
	
	/**
	 * Loads the service lists of post startup's for manual additions
	 *
	 * @return The list of guice post startups
	 */
	public @NotNull
	Set<IGuiceModule> loadIGuiceModules()
	{
		return getLoader(IGuiceModule.class, true, ServiceLoader.load(IGuiceModule.class));
	}
	
	/**
	 * Loads the service lists of guice configurators (before pre-startup) for manual additions
	 *
	 * @return The list of guice configs
	 */
	public @NotNull
	Set<IGuiceConfigurator> loadIGuiceConfigs()
	{
		return getLoader(IGuiceConfigurator.class, true, ServiceLoader.load(IGuiceConfigurator.class));
	}
	
	/**
	 * Method loadPreStartups gets the pre startups and loads them up
	 */
	private void loadPreStartups()
	{
		Set<IGuicePreStartup> preStartups = loadPreStartupServices();
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
	 * Returns the loader for anything that is located locally in guice context
	 * replacing with set load methods instead for each type
	 *
	 * @param loaderType The service type
	 * @param <T>        The type
	 * @return A set of them
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T extends Comparable<T>> Set<T> getLoader(Class<T> loaderType, ServiceLoader<T> serviceLoader)
	{
		if (!getAllLoadedServices().containsKey(loaderType))
		{
			Set<T> loader;
			if (GuiceContext.buildingInjector || injector == null)
			{
				loader = IDefaultService.loaderToSetNoInjection(serviceLoader);
			}
			else
			{
				loader = IDefaultService.loaderToSet(serviceLoader);
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
	@SuppressWarnings("WeakerAccess")
	@NotNull
	public static Map<Class, Set> getAllLoadedServices()
	{
		return allLoadedServices;
	}
	
	/**
	 * If this scanner is registered to run asynchronously
	 *
	 * @return
	 */
	public boolean isAsync()
	{
		return async;
	}
	
	/**
	 * Sets if the scanner must run asynchronously
	 *
	 * @param async
	 */
	public void setAsync(boolean async)
	{
		this.async = async;
	}
}
