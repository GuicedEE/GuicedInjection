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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
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

    private static Injector injector;

    public GuiceContext()
    {
        log.info("Starting Up Guice Context");
        this.classLoader = getClass().getClassLoader();

        log.config("Starting up reflectors");
        reflect();
        log.config("Reflector configured");
    }

    private ClassLoader classLoader;

    public GuiceContext(ClassLoader classLoader)
    {
        this();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    @Provides
    @Override
    public Injector getInjector()
    {
        return produceInjector();
    }

    @Provides
    public Reflections getReflections()
    {
        return reflect();
    }

    private static Reflections reflections;

    public static Reflections reflect()
    {
        if (reflections == null)
        {
            List<ClassLoader> classLoadersList = new ArrayList<>();
            classLoadersList.add(ClasspathHelper.contextClassLoader());
            classLoadersList.add(ClasspathHelper.staticClassLoader());
            reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner(), new TypeAnnotationsScanner())
                    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))));
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
        if (injector == null)
        {
            log.info("Starting up Guice");
            log.info("Loading All Default Binders (that extend GuiceDefaultBinder)");
            GuiceInjectorModule defaultInjection;
            defaultInjection = new GuiceInjectorModule();
            log.info("Loading All Site Binders (that extend GuiceSiteBinder)");
            GuiceSiteInjectorModule siteInjection;
            siteInjection = new GuiceSiteInjectorModule();
            siteInjection.setClassLoader(classLoader);
            injector = Guice.createInjector(defaultInjection, siteInjection);
            log.info("Finished with Guice");
        }
        return injector;
    }

    public static Injector Injector()
    {

        if (injector == null)
        {
            log.warning("Accessing the Injcetor statically before constructing dynamically. Classes may be missed");
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
}
