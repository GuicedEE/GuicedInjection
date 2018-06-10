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
package com.jwebmp.guiceinjection.abstractions;

import com.google.inject.*;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.servlet.ServletModule;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeListener;
import com.jwebmp.guiceinjection.GuiceContext;
import com.jwebmp.guiceinjection.Reflections;
import com.jwebmp.guiceinjection.interfaces.DefaultModuleMethods;
import com.jwebmp.guiceinjection.interfaces.GuiceSiteBinder;
import com.jwebmp.logger.LogFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads up all the Guice Servlet Binders
 *
 * @author GedMarc
 * @since 12 Dec 2016
 */
public class GuiceSiteInjectorModule
		extends ServletModule
		implements DefaultModuleMethods, Serializable
{


	private static final Logger log = LogFactory.getLog("GuiceSiteInjectorModule");
	private static final long serialVersionUID = 1L;

	private int sortOrder = 100;

	public GuiceSiteInjectorModule()
	{
		//Nothing needed
	}

	/**
	 * Gets the current sort order, default 100
	 *
	 * @return
	 */
	public int getSortOrder()
	{
		return sortOrder;
	}

	/**
	 * Sets the current sort order default 100
	 *
	 * @param sortOrder
	 */
	public void setSortOrder(int sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPattern
	 * @param morePatterns
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveSite(String urlPattern, String... morePatterns)
	{
		return serve(urlPattern, morePatterns);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPatterns
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveSite(Iterable<String> urlPatterns)
	{
		return serve(urlPatterns);
	}

	/**
	 * regexes - Any Java-style regular expressions. Since: 4.1
	 *
	 * @param regex
	 * @param regexes
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveSiteRegex(String regex, String... regexes)
	{
		return serveRegex(regex, regexes);
	}

	/**
	 * regexes - Any Java-style regular expressions. Since: 4.1
	 *
	 * @param regexes
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveSiteRegex(Iterable<String> regexes)
	{
		return serveRegex(regexes);
	}

	/**
	 * Runs the binders
	 */
	@Override
	public void configureServlets()
	{
		runBinders();
	}

	/**
	 * Runs the binders for the system
	 */
	public void runBinders()
	{
		Reflections reflections = GuiceContext.reflect();
		Set<Class<? extends GuiceSiteBinder>> siteBinders = reflections.getSubTypesOf(GuiceSiteBinder.class);
		log.log(Level.INFO, "Total number of site injectors - {0}", siteBinders.size());
		List<GuiceSiteBinder> objects = new ArrayList<>();
		siteBinders.forEach(next ->
		                    {
			                    try
			                    {
				                    GuiceSiteBinder obj = next.newInstance();
				                    objects.add(obj);
			                    }
			                    catch (InstantiationException | IllegalAccessException ex)
			                    {
				                    log.log(Level.SEVERE, "Couldn't load module from sets" + siteBinders.toString(), ex);
			                    }
		                    });
		if (!objects.isEmpty())
		{
			objects.forEach(obj ->
			                {
				                log.log(Level.CONFIG, "Loading Guice Servlet Configuration {0}", obj.getClass()
				                                                                                    .getSimpleName());
				                obj.onBind(this);
				                log.log(Level.FINE, "Loaded Guice Servlet Configuration {0}", obj.getClass()
				                                                                                 .getSimpleName());
			                });
		}
	}

	/**
	 * Binds to a given scope
	 *
	 * @param scopeAnnotation
	 * @param scope
	 */
	@Override
	public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope)
	{
		super.bindScope(scopeAnnotation, scope);
	}

	/**
	 * Binds to a given guice key
	 *
	 * @param <T>
	 * @param key
	 *
	 * @return
	 */
	@Override
	public <T> LinkedBindingBuilder<T> bind(Key<T> key)
	{
		return super.bind(key);
	}

	/**
	 * Binds to a given type literal
	 *
	 * @param <T>
	 * @param typeLiteral
	 *
	 * @return
	 */
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral)
	{
		return super.bind(typeLiteral);
	}

	/**
	 * Binds to a given class
	 *
	 * @param <T>
	 * @param clazz
	 *
	 * @return
	 */
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz)
	{
		return super.bind(clazz);
	}

	/**
	 * Binds to a given constant
	 *
	 * @return
	 */
	@Override
	public AnnotatedConstantBindingBuilder bindConstant()
	{
		return super.bindConstant();
	}

	/**
	 * Installs a given module
	 *
	 * @param module
	 */
	@Override
	public void install(Module module)
	{
		super.install(module);
	}

	/**
	 * Bind listener
	 *
	 * @param typeMatcher
	 * @param listener
	 */
	@Override
	public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener)
	{
		super.bindListener(typeMatcher, listener);
	}

	/**
	 * Bind listener
	 *
	 * @param bindingMatcher
	 * @param listener
	 */
	@Override
	public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener)
	{
		super.bindListener(bindingMatcher, listener);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPatterns
	 *
	 * @return
	 */
	public FilterKeyBindingBuilder filter$(Iterable<String> urlPatterns)
	{
		return super.filter(urlPatterns);
	}

	/**
	 * regexes - Any Java-style regular expressions. Since: 4.1
	 *
	 * @param regex
	 * @param regexes
	 *
	 * @return
	 */
	public FilterKeyBindingBuilder filterRegex$(String regex, String... regexes)
	{
		return super.filterRegex(regex, regexes);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param regexes
	 *
	 * @return
	 */
	public FilterKeyBindingBuilder filterRegex$(Iterable<String> regexes)
	{
		return super.filterRegex(regexes);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPattern
	 * @param morePatterns
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serve$(String urlPattern, String... morePatterns)
	{
		return super.serve(urlPattern, morePatterns);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPatterns
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serve$(Iterable<String> urlPatterns)
	{
		return super.serve(urlPatterns);
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param regex
	 * @param regexes
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveRegex$(String regex, String... regexes)
	{
		return super.serveRegex(regex, regexes);
	}

	/**
	 * regexes - Any Java-style regular expressions. Since: 4.1
	 *
	 * @param regexes
	 *
	 * @return
	 */
	public ServletKeyBindingBuilder serveRegex$(Iterable<String> regexes)
	{
		return super.serveRegex(regexes);
	}

	/**
	 * This method only works if you are using the GuiceServletContextListener to create your injector. Otherwise, it returns null.
	 *
	 * @return
	 */
	public javax.servlet.ServletContext getServletContext$()
	{
		return super.getServletContext();
	}

	/**
	 * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
	 *
	 * @param urlPattern
	 * @param morePatterns
	 *
	 * @return
	 */
	public FilterKeyBindingBuilder filter$(String urlPattern, String... morePatterns)
	{
		return super.filter(urlPattern, morePatterns);
	}

	/**
	 * Binds a method intercepter
	 *
	 * @param classMatcher
	 * @param methodMatcher
	 * @param interceptors
	 */
	public void bindInterceptor$(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, org.aopalliance.intercept.MethodInterceptor... interceptors)
	{
		super.bindInterceptor(classMatcher, methodMatcher, interceptors);
	}

}
