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
package za.co.mmagon.guiceinjection;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.oracle.jaxb21.Persistence;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import za.co.mmagon.guiceinjection.abstractions.GuiceSiteInjectorModule;
import za.co.mmagon.guiceinjection.annotations.GuiceInjectorModuleMarker;
import za.co.mmagon.guiceinjection.annotations.GuicePostStartup;
import za.co.mmagon.guiceinjection.annotations.GuicePreStartup;
import za.co.mmagon.guiceinjection.annotations.JaxbContext;
import za.co.mmagon.guiceinjection.logging.LogSingleLineFormatter;
import za.co.mmagon.guiceinjection.scanners.FileContentsScanner;
import za.co.mmagon.guiceinjection.scanners.PackageContentsScanner;

import javax.annotation.Nullable;
import javax.servlet.ServletContextEvent;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
public class GuiceContext extends GuiceServletContextListener
{
	public static final Key<JAXBContext> PERSISTENCE_CONTEXT_KEY = Key.get(JAXBContext.class, JaxbContext.class);
	private static final Logger log = Logger.getLogger("GuiceContext");
	/**
	 * This particular instance of the class
	 */
	private static GuiceContext instance;
	/**
	 * A list of all the specifically excluded jar files (to skip unzip)
	 */

	private static ExecutorService asynchronousFileLoaderExectionsService = Executors.newWorkStealingPool();
	private static ExecutorService asynchronousPersistenceFileLoaderExecutionService = Executors.newWorkStealingPool();
	private static ExecutorService postLoaderExecutionService = Executors.newWorkStealingPool();

	/**
	 * The building injector
	 */
	private static boolean buildingInjector = false;
	/**
	 * If the references are built or not
	 */
	private static boolean built = false;
	private static JAXBContext persistenceContext;

	static
	{
		for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers())
		{
			handler.setFormatter(new LogSingleLineFormatter());
		}
	}

	/**
	 * The physical injector for the JVM container
	 */
	private Injector injector;

	/**
	 * The actual scanner
	 */
	private FastClasspathScanner scanner;
	/**
	 * The scan result built from everything - the core scanner.
	 */
	private ScanResult scanResult;

	/**
	 * Facade layer for backwards compatibility
	 */
	private Reflections reflections;

	/**
	 * A list of jars to exclude from the scan file for the application
	 */
	private Set<String> excludeJarsFromScan;

	/**
	 * Creates a new Guice context. Not necessary
	 */
	private GuiceContext()
	{
		if (instance == null)
		{
			instance = this;
			log.info("Starting up JAXB Persistence");
			Runnable loadAsync = () ->
			{
				try
				{
					persistenceContext = JAXBContext.newInstance(Persistence.class);
				}
				catch (JAXBException e)
				{
					log.log(Level.SEVERE, "Unable to load Persistence Context JPA 2.1", e);
					persistenceContext = null;
				}
			};
			asynchronousPersistenceFileLoaderExecutionService.submit(loadAsync);
		}
	}

	/**
	 * Reference the Injector Directly
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static synchronized Injector inject()
	{
		if (!built && !buildingInjector && context().injector == null)
		{
			buildingInjector = true;
			log.info("Starting up Injections");
			log.config("Startup Executions....");
			Set<Class<? extends GuicePreStartup>> pres = reflect().getSubTypesOf(GuicePreStartup.class);
			List<GuicePreStartup> startups = new ArrayList<>();
			for (Class<? extends GuicePreStartup> pre : pres)
			{
				GuicePreStartup pr = null;
				try
				{
					pr = pre.newInstance();
				}
				catch (InstantiationException | IllegalAccessException e)
				{
					log.log(Level.SEVERE, "Error trying to create Pre Startup Class (newInstance) - " + pre.getCanonicalName(), e);
				}
				startups.add(pr);
			}
			startups.sort(Comparator.comparing(GuicePreStartup::sortOrder));
			log.log(Level.FINE, "Total of [{0}] startup modules.", startups.size());
			startups.forEach(GuicePreStartup::onStartup);
			log.info("Finished Startup Execution");

			log.info("Loading All Default Binders (that extend GuiceDefaultBinder)");

			za.co.mmagon.guiceinjection.abstractions.GuiceInjectorModule defaultInjection;
			defaultInjection = new za.co.mmagon.guiceinjection.abstractions.GuiceInjectorModule();
			log.info("Loading All Site Binders (that extend GuiceSiteBinder)");

			GuiceSiteInjectorModule siteInjection;
			siteInjection = new GuiceSiteInjectorModule();

			int customModuleSize = reflect().getTypesAnnotatedWith(GuiceInjectorModuleMarker.class).size();
			log.log(Level.CONFIG, "Loading [{0}] Custom Modules", customModuleSize);
			ArrayList<Module> customModules = new ArrayList<>();
			Module[] cModules;

			for (Class<?> aClass : reflect().getTypesAnnotatedWith(GuiceInjectorModuleMarker.class))
			{
				try
				{
					Class<? extends AbstractModule> next = (Class<? extends AbstractModule>) aClass;
					log.log(Level.CONFIG, "Adding Module [{0}]", next.getCanonicalName());
					Module moduleInstance = next.newInstance();
					customModules.add(moduleInstance);
				}
				catch (InstantiationException | IllegalAccessException ex)
				{
					if (Modifier.isAbstract(aClass.getModifiers()))
					{
						continue;
					}
					Logger.getLogger(GuiceContext.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			customModules.add(0, siteInjection);
			customModules.add(0, defaultInjection);

			cModules = new Module[customModules.size()];
			cModules = customModules.toArray(cModules);

			context().injector = Guice.createInjector(cModules);
			buildingInjector = false;
			log.info("Post Startup Executions....");
			Set<Class<? extends GuicePostStartup>> closingPres = reflect().getSubTypesOf(GuicePostStartup.class);
			List<GuicePostStartup> postStartups = new ArrayList<>();
			Map<Integer, List<GuicePostStartup>> postStartupGroups = new TreeMap<>();
			closingPres.forEach(closingPre -> postStartups.add(GuiceContext.getInstance(closingPre)));
			postStartups.sort(Comparator.comparing(GuicePostStartup::sortOrder));
			log.log(Level.CONFIG, "Total of [{0}] startup modules.", postStartups.size());
			postStartups.forEach(a ->
			                     {
				                     Integer sortOrder = a.sortOrder();
				                     postStartupGroups.computeIfAbsent(sortOrder, k -> new ArrayList<>());
			                     });
			postStartupGroups.keySet().forEach(integer ->
			                                   {
				                                   postLoaderExecutionService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
				                                   List<GuicePostStartup> st = postStartupGroups.get(integer);
				                                   st.forEach(a ->
				                                              {
					                                              postLoaderExecutionService.execute(a::postLoad);
				                                              });
				                                   postLoaderExecutionService.shutdown();
				                                   try
				                                   {
					                                   postLoaderExecutionService.awaitTermination(20, TimeUnit.SECONDS);
				                                   }
				                                   catch (InterruptedException e)
				                                   {
					                                   log.log(Level.SEVERE, "Could not execute asynchronous post loads", e);
				                                   }
			                                   });
			postStartups.forEach(GuicePostStartup::postLoad);
			log.info("Finished Post Startup Execution");
			log.info("System Ready");
			built = true;
		}
		else
		{
			log.fine("Premature call to GuiceContext.inject. Injector is still currently building, are you calling guice context from a constructor? consider using init() or preconfigure()");
		}

		buildingInjector = false;
		return context().injector;
	}

	/**
	 * Execute on Destroy
	 */
	public static void destroy()
	{
		context().reflections = null;
		context().scanResult = null;
		context().scanner = null;
		context().injector = null;
		GuiceContext.instance = null;
	}

	/**
	 * Returns the actual context instance, provides access to methods existing a bit deeper
	 *
	 * @return
	 */
	public static GuiceContext context()
	{
		if (instance == null)
		{
			instance = new GuiceContext();
		}
		return instance;
	}

	/**
	 * Gets a new injected instance of a class
	 *
	 * @param <T>
	 * @param type
	 *
	 * @return
	 */
	@NotNull
	public static <T> T getInstance(Class<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * Gets a new specified instance from a give key
	 *
	 * @param <T>
	 * @param type
	 *
	 * @return
	 */
	@NotNull
	public static <T> T getInstance(Key<T> type)
	{
		return inject().getInstance(type);
	}

	/**
	 * If context can be used
	 *
	 * @return
	 */
	public static boolean isReady()
	{
		return isBuilt() && isBuildingInjector();
	}

	/**
	 * If the context is built
	 *
	 * @return
	 */
	public static boolean isBuilt()
	{
		return built;
	}

	/**
	 * If the context is built
	 *
	 * @param built
	 */
	public static void setBuilt(boolean built)
	{
		GuiceContext.built = built;
	}

	/**
	 * If the context is currently still building the injector
	 *
	 * @return
	 */
	public static boolean isBuildingInjector()
	{
		return buildingInjector;
	}

	/**
	 * Returns an asychronous file load Execution Service (work steal)
	 *
	 * @return
	 */
	public static ExecutorService getAsynchronousFileLoaderExectionsService()
	{
		return asynchronousFileLoaderExectionsService;
	}

	/**
	 * Returns a threaded loader specifically for persistence units Execution Service (work steal)
	 * Allows you to load models asynchronously to your application
	 *
	 * @return
	 */
	public static ExecutorService getAsynchronousPersistenceFileLoaderExecutionService()
	{
		return asynchronousPersistenceFileLoaderExecutionService;
	}

	/**
	 * Returns the instance of the JAXB Context
	 *
	 * @return
	 */
	public static JAXBContext getPersistenceContext()
	{
		return persistenceContext;
	}

	/**
	 * Returns the current scan result
	 *
	 * @return
	 */
	@Nullable
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
	 */
	public void setScanResult(ScanResult scanResult)
	{
		GuiceContext.context().scanResult = scanResult;
	}

	/**
	 * Starts up Guice and the scanner
	 */
	public void loadScanner()
	{
		log.info("Starting up classpath scanner");
		LocalDateTime start = LocalDateTime.now();
		scanner = new FastClasspathScanner(getPackagesList());
		scanner.enableFieldInfo();
		scanner.enableFieldAnnotationIndexing();
		scanner.enableFieldTypeIndexing();
		scanner.enableMethodAnnotationIndexing();
		scanner.enableMethodInfo();
		scanner.ignoreFieldVisibility();
		scanner.ignoreMethodVisibility();
		registerScanQuickFiles(scanner);
		scanResult = scanner.scan(Runtime.getRuntime().availableProcessors());
		LocalDateTime finish = LocalDateTime.now();
		log.info("Classpath Scanner Completed. Took [" + (finish.getLong(ChronoField.MILLI_OF_SECOND) - start.getLong(ChronoField.MILLI_OF_SECOND)) + "] millis.");
	}

	/**
	 * Returns a complete list of generic exclusions
	 *
	 * @return
	 */
	private String[] getPackagesList()
	{
		log.config("Starting scan for package monitors. Services registered with PackageContentsScanner will be found.");
		if (excludeJarsFromScan == null || excludeJarsFromScan.isEmpty())
		{
			excludeJarsFromScan = new HashSet<>();
			ServiceLoader<PackageContentsScanner> exclusions = ServiceLoader.load(PackageContentsScanner.class);
			for (PackageContentsScanner exclusion : exclusions)
			{
				excludeJarsFromScan.addAll(exclusion.searchFor());
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
	 */
	@SuppressWarnings("unchecked")
	private void registerScanQuickFiles(FastClasspathScanner scanner)
	{
		log.config("Starting File Contents Scanner. Services registered with FileContentsScanner will be found.");
		ServiceLoader<FileContentsScanner> fileScanners = ServiceLoader.load(FileContentsScanner.class);
		int found = 0;
		for (FileContentsScanner fileScanner : fileScanners)
		{
			fileScanner.onMatch().forEach(scanner::matchFilenamePathLeaf);
			found++;
		}
		log.config("File Contents Scanner Matchers have been registered. Total Content Scanners [" + found + "].");
	}

	/**
	 * Returns the current classpath scanner
	 *
	 * @return
	 */
	public FastClasspathScanner getScanner()
	{
		return scanner;
	}

	/**
	 * Sets the classpath scanner
	 *
	 * @param scanner
	 */
	public static void setScanner(FastClasspathScanner scanner)
	{
		context().scanner = scanner;
	}

	/**
	 * Initializes Guice Context post Startup Beans
	 *
	 * @param servletContextEvent
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		inject();
	}

	/**
	 * Maps the injector class to the injector
	 *
	 * @return
	 */
	@Override
	public Injector getInjector()
	{
		return inject();
	}

	/**
	 * Sets the given injector to this context
	 *
	 * @param injector
	 */
	public void setInjector(Injector injector)
	{
		this.injector = injector;
	}

	/**
	 * Returns the fully populated reflections object
	 *
	 * @return
	 */
	public Reflections getReflections()
	{
		return reflect();
	}

	/**
	 * Builds a reflection object if one does not exist
	 *
	 * @return
	 */
	public static Reflections reflect()
	{
		if (context().reflections == null)
		{
			context().reflections = new Reflections();
		}
		return context().reflections;
	}
}
