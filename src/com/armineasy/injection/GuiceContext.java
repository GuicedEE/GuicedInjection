/*
 * Copyright (C) 2016 GedMarc
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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.*;
import javax.servlet.ServletContextEvent;
import org.reflections.Reflections;
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

    private static final Logger log = Logger.getLogger("GuiceServletContextListener");
    /**
     * The physical injector for the JVM container
     */
    private static Injector injector;
    /**
     * The actual reflections object
     */
    private static Reflections reflections;

    /**
     * Creates a new Guice context. Should only happen once
     */
    public GuiceContext()
    {
        log.info("Starting Up Guice Context");
        log.config("Starting up reflectors");
        reflect();
        log.config("Reflector configured");
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
        if (reflections == null)
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
                    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner(), new TypeAnnotationsScanner())
                    .setUrls(urls));
        }
        return reflections;
    }

    /**
     * Returns an instance of the injector
     *
     * @return
     */
    public Injector produceInjector()
    {
        return Injector();
    }

    /**
     * Static reference to build an injector. May miss some classes if called at the wrong time.
     * Better to use a class instantiator
     *
     * @return
     */
    public static Injector Injector()
    {
        if (injector == null)
        {
            log.info("Starting up Guice");
            log.info("Loading All Default Binders (that extend GuiceDefaultBinder)");
            GuiceInjectorModule defaultInjection;
            defaultInjection = new GuiceInjectorModule();
            log.info("Loading All Site Binders (that extend GuiceSiteBinder)");
            GuiceSiteInjectorModule siteInjection;
            siteInjection = new GuiceSiteInjectorModule();
            injector = Guice.createInjector(defaultInjection, siteInjection);
            log.info("Finished with Guice");
        }
        return injector;
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
        reflections = null;
        super.contextDestroyed(servletContextEvent);
    }
}
