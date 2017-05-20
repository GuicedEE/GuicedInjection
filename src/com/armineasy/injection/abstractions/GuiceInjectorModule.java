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
package com.armineasy.injection.abstractions;

import com.armineasy.injection.GuiceContext;
import com.armineasy.injection.interfaces.DefaultModuleMethods;
import com.armineasy.injection.interfaces.GuiceDefaultBinder;
import com.google.inject.*;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 * Is a default injector module for Guice
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public class GuiceInjectorModule extends AbstractModule implements DefaultModuleMethods
{

    private static final Logger log = Logger.getLogger("GuiceInjectorModule");

    /**
     * Constructs a new instance of the module
     */
    public GuiceInjectorModule()
    {
        //Nothing Needed
    }

    /**
     * Executes the linked binders to perform any custom binding
     */
    public void runBinders()
    {
        log.log(Level.CONFIG, "Running Default Injection Binders");
        Reflections reflections = GuiceContext.reflect();
        Set<Class<? extends GuiceDefaultBinder>> sets = reflections.getSubTypesOf(GuiceDefaultBinder.class);
        log.log(Level.INFO, "Total number of default injectors going to call {0}", sets.size());
        List<GuiceDefaultBinder> objects = new ArrayList<>();
        sets.forEach(next ->
        {
            try
            {
                GuiceDefaultBinder obj = next.newInstance();
                objects.add(obj);
            }
            catch (InstantiationException | IllegalAccessException ex)
            {
                log.log(Level.SEVERE, "Couldn;t load module from sets" + sets.toString(), ex);
            }
        });
        if (!objects.isEmpty())
        {
            Collections.sort(objects, objects.get(0));
            objects.stream().forEachOrdered(obj ->
            {
                log.log(Level.CONFIG, "Loading Guice Configuration {0}", obj.getClass().getSimpleName());
                obj.onBind(this);
                log.log(Level.CONFIG, "Finished Guice Configuration {0}", obj.getClass().getSimpleName());
            });
        }
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz)
    {
        return super.bind(clazz);
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(Key<T> key)
    {
        return super.bind(key);
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral)
    {
        return super.bind(typeLiteral);
    }

    @Override
    public AnnotatedConstantBindingBuilder bindConstant()
    {
        return super.bindConstant();
    }

    @Override
    public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener)
    {
        super.bindListener(bindingMatcher, listener);
    }

    @Override
    public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener)
    {
        super.bindListener(typeMatcher, listener);
    }

    @Override
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope)
    {
        super.bindScope(scopeAnnotation, scope);
    }

    @Override
    public Binder binder()
    {
        return super.binder();
    }

    @Override
    protected void configure()
    {
        runBinders();
    }

}
