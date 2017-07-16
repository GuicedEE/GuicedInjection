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
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import java.util.*;
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

    private static GuiceContext instance;

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
    private Injector injector;

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
    private List<String> excludeJarsFromScan;

    /**
     * Returns the current scan result
     *
     * @return
     */
    public ScanResult getScanResult()
    {
        if (scanResult == null)
        {
            GuiceStartup();
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
     * Creates a new Guice context. Not necessary
     */
    private GuiceContext()
    {
        if (instance == null)
        {
            instance = this;
        }
    }

    private void GuiceStartup()
    {
        log.info("Starting up classpath scanner");
        StringBuilder sb = new StringBuilder();
        if (excludeJarsFromScan == null || excludeJarsFromScan.isEmpty())
        {
            excludeJarsFromScan = new ArrayList<>();
            excludeJarsFromScan.add("-com.fasterxml.jackson");
            excludeJarsFromScan.add("-com.google.common");
            excludeJarsFromScan.add("-com.google.inject");
            excludeJarsFromScan.add("-com.microsoft.sqlserver");
            excludeJarsFromScan.add("-com.sun.enterprise.glassfish");
            excludeJarsFromScan.add("-com.sun.enterprise.module");
            excludeJarsFromScan.add("-com.sun.jdi");
            excludeJarsFromScan.add("-edu.umd.cs.findbugs");
            excludeJarsFromScan.add("-io.github.lukehutch.fastclasspathscanner");
            excludeJarsFromScan.add("-javassist");
            excludeJarsFromScan.add("-net.sf.qualitycheck");
            excludeJarsFromScan.add("-net.sf.uadetector");
            excludeJarsFromScan.add("-org.aopalliance");
            excludeJarsFromScan.add("-org.apache.catalina");
            excludeJarsFromScan.add("-org.apache.commons");
            excludeJarsFromScan.add("-org.apache.derby");
            excludeJarsFromScan.add("-org.glassfish");

            //glassfish jar defaultsglassfish.jar
            excludeJarsFromScan.add("-org.ietf");
            excludeJarsFromScan.add("-org.jboss");
            excludeJarsFromScan.add("-org.jvnet");
            excludeJarsFromScan.add("-org.slf4j");
            excludeJarsFromScan.add("-org.w3c");
            excludeJarsFromScan.add("-org.xml.sax");

        }
        String[] exclusions = new String[excludeJarsFromScan.size()];
        exclusions = excludeJarsFromScan.toArray(exclusions);

        scanner = new FastClasspathScanner(exclusions);
        scanner.enableFieldInfo();
        scanner.enableFieldAnnotationIndexing();
        scanner.enableFieldTypeIndexing();
        scanner.enableMethodAnnotationIndexing();
        scanner.enableMethodInfo();
        scanner.ignoreFieldVisibility();
        scanner.ignoreMethodVisibility();
        scanResult = scanner.scan(5);
        scanResult.getNamesOfAllStandardClasses().forEach(log::finer);

        log.info("Classpath Scanner Completed");
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
        log.info("Context Found, Reloading class path ");
        log.info("Starting Up Servlet Context");
        Date startDate = new Date();
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

    /**
     * Reference the Injector Directly
     *
     * @return
     */
    public static synchronized Injector inject()
    {
        if (context().injector == null)
        {
            if (!buildingInjector)
            {
                buildingInjector = true;
                log.info("Starting up Injections");
                log.config("Startup Executions....");
                Set<Class<? extends GuicePreStartup>> pres = reflect().getSubTypesOf(GuicePreStartup.class);
                List<GuicePreStartup> startups = new ArrayList<>();
                pres.stream().map(pre -> GuiceContext.getInstance(pre)).forEachOrdered(startups::add);
                Collections.sort(startups, (GuicePreStartup o1, GuicePreStartup o2) -> o1.sortOrder().compareTo(o2.sortOrder()));
                log.log(Level.FINE, "Total of [{0}] startup modules.", startups.size());
                startups.forEach(GuicePreStartup::onStartup);
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
                cModules = customModules.toArray(cModules);

                //  context().injector = Guice.createInjector(cModules);
                buildingInjector = false;
                log.config("Post Startup Executions....");
                Set<Class<? extends GuicePostStartup>> closingPres = reflect().getSubTypesOf(GuicePostStartup.class);
                List<GuicePostStartup> postStartups = new ArrayList<>();
                closingPres.stream().map(pre -> GuiceContext.getInstance(pre)).forEachOrdered(postStartups::add);
                Collections.sort(postStartups, (GuicePostStartup o1, GuicePostStartup o2) -> o1.sortOrder().compareTo(o2.sortOrder()));
                log.log(Level.FINE, "Total of [{0}] startup modules.", postStartups.size());
                postStartups.stream().forEach(GuicePostStartup::postLoad);
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
        return context().injector;
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
        System.out.println("Destroying Context");
        context().reflections = null;
        context().scanResult = null;
        context().scanner = null;
        context().injector = null;
        context().instance = null;
        System.out.println("Finalized Destroy");
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
