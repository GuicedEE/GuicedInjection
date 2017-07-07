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
package com.armineasy.injection;

import com.armineasy.injection.abstractions.GuiceInjectorModule;
import com.armineasy.injection.abstractions.GuiceSiteInjectorModule;
import com.armineasy.injection.annotations.GuicePostStartup;
import com.armineasy.injection.annotations.GuicePreStartup;
import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;

/**
 * Provides an interface for reflection and injection in one.
 * <p>
 * Use reflect() to access the class library, or inject() to get the injector for any instance
 *
 * @author GedMarc
 * @since Nov 14, 2016
 * @version 1.0
 *
 */
public class GuiceContext extends GuiceServletContextListener
{

    private static final Logger log = Logger.getLogger("GuiceContext");

    private static final GuiceContext instance = new GuiceContext();

    /**
     * The building injector
     */
    private static boolean buildingInjector = false;
    /**
     * If the references are built or not
     */
    private static boolean built = false;
    /**
     * The physical injector for the JVM container
     */
    private static transient Injector injector;

    private static FastClasspathScanner scanner;

    protected static ScanResult scanResult;

    /**
     * Facade layer for backwards compatibility
     */
    private static Reflections reflections;

    private static List<String> excludeJarsFromScan;

    /**
     * Creates a new Guice context. Not necessary
     */
    private GuiceContext()
    {
        log.info("Starting up classpath scanner with 15 threads");
        StringBuilder sb = new StringBuilder();
        if (excludeJarsFromScan == null)
        {
            excludeJarsFromScan = new CopyOnWriteArrayList<>();
            excludeJarsFromScan.add("fast-classpath-scanner*.jar");
            excludeJarsFromScan.add("guava-*.jar");
            excludeJarsFromScan.add("guice-*.jar");
            excludeJarsFromScan.add("jackson-*.jar");
            excludeJarsFromScan.add("javassist-*.jar");
            excludeJarsFromScan.add("jsr305-*.jar");
            excludeJarsFromScan.add("jsr311-*.jar");
            excludeJarsFromScan.add("animal-sniffer-*.jar");
            excludeJarsFromScan.add("annotations-*.jar");
            excludeJarsFromScan.add("aopalliance-*.jar");
            excludeJarsFromScan.add("aspectjrt-*.jar");
            excludeJarsFromScan.add("error_prone_annotations-*.jar");
            excludeJarsFromScan.add("j2odbc-*.jar");
            excludeJarsFromScan.add("javax.inject-*.jar");
            excludeJarsFromScan.add("jcabi-*.jar");
            excludeJarsFromScan.add("sl4j-*.jar");
            excludeJarsFromScan.add("validation-api-*.jar");
        }
        for (String excludedJar : excludeJarsFromScan)
        {
            sb.append("-jar:").append(excludedJar).append(",");
        }
        scanner = new FastClasspathScanner();
        excludeJarsFromScan = new CopyOnWriteArrayList<>();
        scanner.enableFieldInfo();
        scanner.enableFieldAnnotationIndexing();
        scanner.enableFieldTypeIndexing();
        scanner.enableMethodAnnotationIndexing();
        scanner.enableMethodInfo();
        scanner.ignoreFieldVisibility();
        scanner.ignoreMethodVisibility();
        scanResult = scanner.scan(15);
        scanResult.getNamesOfAllStandardClasses().forEach(log::finer);
        log.info("Classpath Scanner Completed");
        reflections = new Reflections();
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
     * Initializes Guice Context post Startup Beans
     *
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        log.info("Context Initialized... ");
        log.info("Starting Up Servlet Context");
        Date startDate = new Date();
        reflect(servletContextEvent);
        Date endDate = new Date();
        log.log(Level.FINER, "Reflections loaded successfully. [{0}ms]", endDate.getTime() - startDate.getTime());
        inject();
        log.log(Level.INFO, "Guice loaded successfully. [{0}ms]", endDate.getTime() - startDate.getTime());
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
     * @param events
     *
     * @return
     */
    public static Reflections reflect(ServletContextEvent... events)
    {
        return reflections;
    }

    /**
     * Reference the Injector Directly
     *
     * @return
     */
    public static Injector inject()
    {

        if (injector == null)
        {
            if (buildingInjector == false)
            {
                buildingInjector = true;
                log.info("Starting up Injections");
                log.config("Startup Executions....");
                Set<Class<? extends GuicePreStartup>> pres = reflect().getSubTypesOf(GuicePreStartup.class);
                List<GuicePreStartup> startups = new ArrayList<>();
                pres.stream().map(pre -> GuiceContext.getInstance(pre)).forEachOrdered(happen ->
                {
                    startups.add(happen);
                });
                Collections.sort(startups, (GuicePreStartup o1, GuicePreStartup o2) -> o1.sortOrder().compareTo(o2.sortOrder()));
                log.log(Level.FINE, "Total of [{0}] startup modules.", startups.size());
                startups.forEach(startup ->
                {
                    startup.onStartup();
                });
                log.info("Finished Startup Execution");
                log.config("Loading All Default Binders (that extend GuiceDefaultBinder)");

                GuiceInjectorModule defaultInjection;
                defaultInjection = new GuiceInjectorModule();
                log.config("Loading All Site Binders (that extend GuiceSiteBinder)");

                GuiceSiteInjectorModule siteInjection;
                siteInjection = new GuiceSiteInjectorModule();

                int customModuleSize = reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class).size();
                log.log(Level.CONFIG, "Loading [{0}] Custom Modules", customModuleSize);
                ArrayList<Module> customModules = new ArrayList<>();
                Module[] cModules;
                for (Iterator<Class<?>> iterator = reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class).iterator(); iterator.hasNext();)
                {
                    try
                    {
                        Class<? extends AbstractModule> next = (Class<? extends AbstractModule>) iterator.next();
                        log.log(Level.FINE, "Adding Module [{0}]", next.getCanonicalName());
                        Module moduleInstance = next.newInstance();
                        customModules.add(moduleInstance);
                    }
                    catch (InstantiationException | IllegalAccessException ex)
                    {
                        Logger.getLogger(GuiceContext.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                customModules.add(0, siteInjection);
                customModules.add(0, defaultInjection);

                cModules = new Module[customModules.size()];
                cModules = (Module[]) customModules.toArray(cModules);

                injector = Guice.createInjector(cModules);
                buildingInjector = false;
                log.config("Post Startup Executions....");
                Set<Class<? extends GuicePostStartup>> closingPres = reflect().getSubTypesOf(GuicePostStartup.class);
                List<GuicePostStartup> postStartups = new ArrayList<>();
                closingPres.stream().map(pre -> GuiceContext.getInstance(pre)).forEachOrdered(happen ->
                {
                    postStartups.add(happen);
                });
                Collections.sort(postStartups, (GuicePostStartup o1, GuicePostStartup o2) -> o1.sortOrder().compareTo(o2.sortOrder()));
                log.log(Level.FINE, "Total of [{0}] startup modules.", postStartups.size());
                postStartups.stream().forEach(startup ->
                {
                    log.log(Level.FINE, "Starting up [{0}]", postStartups.getClass().getCanonicalName());
                    startup.postLoad();
                });
                log.config("Finished Post Startup Execution");
                log.info("Finished with Guice");
                built = true;
            }
            else
            {
                log.severe("Premature call to GuiceContext.inject. Injector is still currently building, are you calling guice context from a constructor? consider using init() or preconfigure()");
            }
        }
        buildingInjector = false;
        return injector;
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
     * Removes all references
     *
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        super.contextDestroyed(servletContextEvent);
    }

    /**
     * Sets the referenced reflections library to the attached
     *
     * @param reflections
     */
    public static void setReflections(Reflections reflections)
    {
        GuiceContext.reflections = reflections;
    }

    /**
     * Gets a new injected instance of a class
     *
     * @param <T>
     * @param type
     *
     * @return
     */
    public static <T> T getInstance(Class<T> type)
    {
        try
        {
            return inject().getInstance(type);
        }
        catch (NullPointerException npe)
        {
            log.log(Level.SEVERE, "Unable to return an injector", npe);
            return null;
        }
    }

    /**
     * Gets a new specified instance from a give key
     *
     * @param <T>
     * @param type
     *
     * @return
     */
    public static <T> T getInstance(Key<T> type)
    {
        try
        {
            return inject().getInstance(type);
        }
        catch (NullPointerException npe)
        {
            log.log(Level.SEVERE, "Unable to return an injector", npe);
            return null;
        }
    }

    /**
     * Execute on Destroy
     */
    public static void destroy()
    {
        System.out.println("Destroyed");
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

}
