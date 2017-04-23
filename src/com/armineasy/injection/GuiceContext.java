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
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
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
    private static boolean buildingInjector = false;
    /**
     * The physical injector for the JVM container
     */
    private static transient Injector injector;
    /**
     * The actual reflections object
     */
    private static transient Reflections reflections;

    static
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

        try
        {
            classLoadersList.add(ClasspathHelper.staticClassLoader());
        }
        catch (NoClassDefFoundError classNotFound)
        {
            log.log(Level.SEVERE, "Can't access static class loader, probably not running in a separate jar", classNotFound);
        }
        Collection<URL> urls = new ArrayList<>();
        try
        {
            urls = ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]));
        }
        catch (NoClassDefFoundError classNotFound)
        {
            log.log(Level.SEVERE, "Can't access static class loader, probably not running in a separate jar", classNotFound);
        }
        reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /*
                     * don't exclude Object.class
                 */), new ResourcesScanner(),
                             new TypeAnnotationsScanner(),
                             new FieldAnnotationsScanner()
                //  ,new MethodAnnotationsScanner(),
                //  new MemberUsageScanner(),
                //  new TypeAnnotationsScanner(),
                //  new MethodParameterNamesScanner(),
                //   new MethodParameterScanner(),
                //    new TypeElementsScanner()
                ).setUrls(urls)
        );
    }

    /**
     * Creates a new Guice context. Should only happen once
     */
    public GuiceContext()
    {
        reflect();
    }

    /**
     * Maps the injector class to the injector
     *
     * @return
     */
    @Provides
    @Override
    public Injector getInjector()
    {
        return produceInjector();
    }

    /**
     * Returns the fully populated reflections object
     *
     * @return
     */
    @Provides
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
        return reflections;
    }

    /**
     * Returns an instance of the injector
     *
     * @return
     */
    public Injector produceInjector()
    {
        return inject();
    }

    /**
     * Static reference to build an injector. May miss some classes if called at the wrong time. Better to use a class instantiator
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
                log.info("Loading All Default Binders (that extend GuiceDefaultBinder)");
                GuiceInjectorModule defaultInjection;
                defaultInjection = new GuiceInjectorModule();
                log.info("Loading All Site Binders (that extend GuiceSiteBinder)");
                GuiceSiteInjectorModule siteInjection;
                siteInjection = new GuiceSiteInjectorModule();

                int customModuleSize = reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class).size();
                log.log(Level.INFO, "Loading Custom Modules [{0}]", customModuleSize);
                ArrayList<Module> customModules = new ArrayList<>();
                Module[] cModules;
                for (Iterator<Class<?>> iterator = reflect().getTypesAnnotatedWith(com.armineasy.injection.annotations.GuiceInjectorModule.class).iterator(); iterator.hasNext();)
                {
                    try
                    {
                        Class<? extends AbstractModule> next = (Class<? extends AbstractModule>) iterator.next();
                        log.log(Level.INFO, "Adding Module [{0}]", next.getCanonicalName());
                        Module instance = next.newInstance();
                        customModules.add(instance);
                    }
                    catch (InstantiationException ex)
                    {
                        Logger.getLogger(GuiceContext.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IllegalAccessException ex)
                    {
                        Logger.getLogger(GuiceContext.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                customModules.add(0, siteInjection);
                customModules.add(0, defaultInjection);
                cModules = new Module[customModules.size()];
                cModules = (Module[]) customModules.toArray(cModules);
                injector = Guice.createInjector(cModules);
                log.info("Finished with Guice");
                buildingInjector = false;
            }
            else
            {
                log.info("Premature call to GuiceContext.inject. Injector is still currently building, are you calling guice context from a constructor? consider using init() or preconfigure()");
            }
        }
        buildingInjector = false;
        return injector;
    }

    public static boolean isBuildingInjector()
    {
        return buildingInjector;
    }

    public static void setBuildingInjector(boolean buildingInjector)
    {
        GuiceContext.buildingInjector = buildingInjector;
    }

    /**
     * Removes all references
     *
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        injector = null;
        super.contextDestroyed(servletContextEvent);
    }

    public static void setReflections(Reflections reflections)
    {
        GuiceContext.reflections = reflections;
    }

    public static <T> T getInstance(Class<T> type)
    {
        return inject().getInstance(type);
    }

    public static <T> T getInstance(Key<T> type)
    {
        return inject().getInstance(type);
    }

    public static void destroy()
    {
        reflections = null;
    }
}
