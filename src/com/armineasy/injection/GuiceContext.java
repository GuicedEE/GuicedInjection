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
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Allows injection of the session, request and response
 *
 * @author GedMarc
 * @since Nov 14, 2016
 * @version 1.0
 *
 */
public class GuiceContext extends GuiceServletContextListener
{

    private static final Logger log = Logger.getLogger("GuiceContext");

    /**
     * The building injector
     */
    private static boolean buildingInjector = false;
    /**
     * The physical injector for the JVM container
     */
    private static transient Injector injector;
    /**
     * The actual reflections object
     */
    private static transient Reflections reflections;

    /**
     * Include Sub Type Scanning
     */
    public static boolean includeSubTypesScanner = true;
    /**
     * Include Scan Resource Files
     */
    public static boolean includeResourcesScanner = true;
    /**
     * Include Scan Type Annotations
     */
    public static boolean includeTypeAnnotationsScanner = true;
    /**
     * Include Field Annotations Scanner
     */
    public static boolean includeFieldAnnotationScanner = true;
    /**
     * Include Method Annotation Scanner
     */
    public static boolean includeMethodAnnotationScanner = false;

    /**
     * Creates a new Guice context. Not necessary
     */
    public GuiceContext()
    {
        log.info("Context Constructed... ");
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
        log.log(Level.INFO, "Reflections loaded successfully. [{0}ms]", endDate.getTime() - startDate.getTime());
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
        if (reflections == null)
        {
            log.info("Starting up reflections");
            List<ClassLoader> classLoadersList = new ArrayList<>();
            try
            {
                classLoadersList.add(ClasspathHelper.contextClassLoader());
            }
            catch (NoClassDefFoundError classNotFound)
            {
                log.log(Level.SEVERE, "Can't access context class loader, probably not running in a separate jar", classNotFound);
            }
            Collection<URL> urls = new ArrayList<>();
            if (events != null && events.length > 0)
            {
                log.config("Reflections found servlet context event. Building on WEB-INF");
                try
                {
                    for (ServletContextEvent servletContextEvent : events)
                    {
                        if (servletContextEvent != null)
                        {
                            for (URL url : ClasspathHelper.forWebInfLib(servletContextEvent.getServletContext()))
                            {
                                if (url == null)
                                {
                                    continue;
                                }
                                if (!urls.contains(url))
                                {
                                    urls.add(url);
                                }
                            }
                            urls.add(ClasspathHelper.forWebInfClasses(servletContextEvent.getServletContext()));
                        }
                    }

                }
                catch (Exception e)
                {
                    log.log(Level.SEVERE, "Can't access Java Class Path, probably not running in a separate jar", e);
                }
            }

            if (urls.isEmpty())
            {
                log.config("Reflections is swallowing the class path, no servlet context event supplied");
                try
                {
                    urls.addAll(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])));
                }
                catch (NoClassDefFoundError classNotFound)
                {
                    log.log(Level.SEVERE, "Can't access static class loader, probably not running in a separate jar", classNotFound);
                }
            }

            //;
            //ClasspathHelper.forWebInfClasses(servletContextEvent.getServletContext());
            List<AbstractScanner> scanners = new ArrayList<>();
            if (includeSubTypesScanner)
            {
                scanners.add(new SubTypesScanner(false));
            }
            if (includeResourcesScanner)
            {
                scanners.add(new ResourcesScanner());
            }
            if (includeTypeAnnotationsScanner)
            {
                scanners.add(new TypeAnnotationsScanner());
            }
            /*
             *
             *
             * if (includeFieldAnnotationScanner)
             * {
             * scanners.add(new FieldAnnotationsScanner());
             * }
             *
             * if (includeMethodAnnotationScanner)
             * {
             * scanners.add(new MethodAnnotationsScanner());
             * }
             */
            //  new MemberUsageScanner(),
            //  new TypeAnnotationsScanner(),
            //  new MethodParameterNamesScanner(),
            //   new MethodParameterScanner(),
            //    new TypeElementsScanner()

            AbstractScanner[] scanArrays = new AbstractScanner[scanners.size()];
            scanArrays = scanners.toArray(scanArrays);
            log.fine("Reflections building in-memory grid..");
            reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(scanArrays
                    ).setUrls(urls));
            log.info("Completed loading up reflections");
        }
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
                log.info("Starting up Guice");
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
                        Module instance = next.newInstance();
                        customModules.add(instance);
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
    public static <T> T getInstance(Key<T> type)
    {
        return inject().getInstance(type);
    }

    /**
     * Execute on Destroy
     */
    public static void destroy()
    {
        reflections = null;
    }
}
