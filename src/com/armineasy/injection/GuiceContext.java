/*
 * The MIT License
 *
 * Copyright 2017 Marc Magon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.reflections.scanners.SubTypesScanner;
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

    private static boolean buildingInjector = false;
    /**
     * The physical injector for the JVM container
     */
    private static transient Injector injector;
    /**
     * The actual reflections object
     */
    private static transient Reflections reflections;

    public static boolean includeSubTypesScanner = true;
    public static boolean includeResourcesScanner = true;
    public static boolean includeTypeAnnotationsScanner = true;
    public static boolean includeFieldAnnotationScanner = true;
    public static boolean includeMethodAnnotationScanner = false;

    /**
     * Creates a new Guice context. Should only happen once
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
        log.log(Level.INFO, "Reflections loaded successfully. [{0}]", endDate.getTime() - startDate.getTime());
        reflect();
        try
        {
            super.contextInitialized(servletContextEvent);
        }
        catch (NullPointerException npe)
        {
            log.warning("Null Pointer Exception For Servlet Context Event");
        }
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
        try
        {
            urls.addAll(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])));
        }
        catch (NoClassDefFoundError classNotFound)
        {
            log.log(Level.SEVERE, "Can't access static class loader, probably not running in a separate jar", classNotFound);
        }

        if (events != null && events.length > 0)
        {
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
        //;
        //ClasspathHelper.forWebInfClasses(servletContextEvent.getServletContext());
        List<AbstractScanner> scanners = new ArrayList<>();
        if (includeSubTypesScanner)
        {
            scanners.add(new SubTypesScanner(false));
        }
        if (includeResourcesScanner)
        {
            scanners.add(new SubTypesScanner(false));
        }

        if (includeTypeAnnotationsScanner)
        {
            scanners.add(new SubTypesScanner(false));
        }

        if (includeFieldAnnotationScanner)
        {
            scanners.add(new SubTypesScanner(false));
        }

        if (includeMethodAnnotationScanner)
        {
            scanners.add(new SubTypesScanner(false));
        }
        //  new MemberUsageScanner(),
        //  new TypeAnnotationsScanner(),
        //  new MethodParameterNamesScanner(),
        //   new MethodParameterScanner(),
        //    new TypeElementsScanner()

        AbstractScanner[] scanArrays = new AbstractScanner[scanners.size()];
        scanArrays = scanners.toArray(scanArrays);
        reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(scanArrays
                ).setUrls(urls));
        return reflections;
    }

    /**
     * Static reference to build an injector. May miss some classes if called at
     * the wrong time. Better to use a class instantiator
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
                for (Class<? extends GuicePreStartup> pre : pres)
                {
                    GuicePreStartup happen = GuiceContext.getInstance(pre);
                    startups.add(happen);
                }

                Collections.sort(startups, new Comparator<GuicePreStartup>()
                {
                    @Override
                    public int compare(GuicePreStartup o1, GuicePreStartup o2)
                    {
                        return o1.sortOrder().compareTo(o2.sortOrder());
                    }
                });
                log.log(Level.FINE, "Total of [{0}] startup modules.", startups.size());
                for (GuicePreStartup startup : startups)
                {
                    startup.onStartup();
                }
                log.info("Finished Startup Execution");

                log.config("Loading All Default Binders (that extend GuiceDefaultBinder)");
                GuiceInjectorModule defaultInjection;
                defaultInjection = new GuiceInjectorModule();
                log.config("Loading All Site Binders (that extend GuiceSiteBinder)");
                GuiceSiteInjectorModule siteInjection;
                siteInjection = new GuiceSiteInjectorModule();

                int customModuleSize = reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class).size();
                log.log(Level.CONFIG, "Loading Custom Modules [{0}]", customModuleSize);
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

                log.config("Post Startup Executions....");
                Set<Class<? extends GuicePostStartup>> closingPres = reflect().getSubTypesOf(GuicePostStartup.class);
                List<GuicePostStartup> postStartups = new ArrayList<>();
                for (Class<? extends GuicePostStartup> pre : closingPres)
                {
                    GuicePostStartup happen = GuiceContext.getInstance(pre);
                    postStartups.add(happen);
                }
                Collections.sort(postStartups, new Comparator<GuicePostStartup>()
                {
                    @Override
                    public int compare(GuicePostStartup o1, GuicePostStartup o2)
                    {
                        return o1.sortOrder().compareTo(o2.sortOrder());
                    }
                });
                log.log(Level.FINE, "Total of [{0}] startup modules.", postStartups.size());
                for (GuicePostStartup startup : postStartups)
                {
                    startup.postLoad();
                }
                log.config("Finished Post Startup Execution");

                log.info("Finished with Guice");
                buildingInjector = false;
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
