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
package com.jwebmp.guicedinjection.abstractions;

import com.google.inject.*;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeListener;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.Reflections;
import com.jwebmp.guicedinjection.interfaces.GuiceDefaultBinder;
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
 * Exposes the abstract module methods as public
 *
 * @author GedMarc
 * @since 12 Dec 2016
 */
public class GuiceInjectorModule
		extends AbstractModule
		implements Serializable
{

	private static final Logger log = LogFactory.getLog("GuiceInjectorModule");
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new instance of the module
	 */
	public GuiceInjectorModule()
	{
		//Nothing Needed
	}

	/**
	 * Executes the runBinders method
	 */
	@Override
	protected void configure()
	{
		runBinders();
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
				             log.log(Level.SEVERE, "Couldn't load module from sets" + sets.toString(), ex);
			             }
		             });
		if (!objects.isEmpty())
		{
			objects.sort(objects.get(0));
			objects.forEach(obj ->
			                {
				                log.log(Level.CONFIG, "Loading Guice Configuration {0}", obj.getClass()
				                                                                            .getSimpleName());
				                obj.onBind(this);
				                log.log(Level.CONFIG, "Finished Guice Configuration {0}", obj.getClass()
				                                                                             .getSimpleName());
			                });
		}
	}

	/**
	 * Gets direct access to the underlying {@code Binder}.
	 */
	@Override
	public Binder binder()
	{
		return super.binder();
	}

	/**
	 * @see Binder#bindScope(Class, Scope)
	 */
	@Override
	public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope)
	{
		super.bindScope(scopeAnnotation, scope);
	}

	/**
	 * @see Binder#bind(Key)
	 */
	@Override
	public <T> LinkedBindingBuilder<T> bind(Key<T> key)
	{
		return super.bind(key);
	}

	/**
	 * @see Binder#bind(TypeLiteral)
	 */
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral)
	{
		return super.bind(typeLiteral);
	}

	/**
	 * @see Binder#bind(Class)
	 */
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz)
	{
		return super.bind(clazz);
	}

	/**
	 * @see Binder#bindConstant()
	 */
	@Override
	public AnnotatedConstantBindingBuilder bindConstant()
	{
		return super.bindConstant();
	}

	/**
	 * @see Binder#install(Module)
	 */
	@Override
	public void install(Module module)
	{
		super.install(module);
	}

	/**
	 * @see Binder#addError(String, Object[])
	 */
	@Override
	public void addError(String message, Object... arguments)
	{
		super.addError(message, arguments);
	}

	/**
	 * @see Binder#addError(Throwable)
	 */
	@Override
	public void addError(Throwable t)
	{
		super.addError(t);
	}

	/**
	 * @see Binder#addError(Message)
	 * @since 2.0
	 */
	@Override
	public void addError(Message message)
	{
		super.addError(message);
	}

	/**
	 * @see Binder#requestInjection(Object)
	 * @since 2.0
	 */
	@Override
	public void requestInjection(Object instance)
	{
		super.requestInjection(instance);
	}

	/**
	 * @see Binder#requestStaticInjection(Class[])
	 */
	@Override
	public void requestStaticInjection(Class<?>... types)
	{
		super.requestStaticInjection(types);
	}

	/**
	 * @see Binder#bindInterceptor(com.google.inject.matcher.Matcher,
	 *        com.google.inject.matcher.Matcher, org.aopalliance.intercept.MethodInterceptor[])
	 */
	@Override
	public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, org.aopalliance.intercept.MethodInterceptor... interceptors)
	{
		binder().bindInterceptor(classMatcher, methodMatcher, interceptors);
	}

	/**
	 * @see Binder#getProvider(Key)
	 * @since 2.0
	 */
	@Override
	public <T> Provider<T> getProvider(Key<T> key)
	{
		return super.getProvider(key);
	}

	/**
	 * @see Binder#getProvider(Class)
	 * @since 2.0
	 */
	@Override
	public <T> Provider<T> getProvider(Class<T> type)
	{
		return super.getProvider(type);
	}

	/**
	 * @see Binder#bindListener(com.google.inject.matcher.Matcher, com.google.inject.spi.TypeListener)
	 * @since 2.0
	 */
	@Override
	public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener)
	{
		super.bindListener(typeMatcher, listener);
	}

	/**
	 * @see Binder#bindListener(com.google.inject.matcher.Matcher, com.google.inject.spi.TypeListener)
	 * @since 2.0
	 */
	@Override
	public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener)
	{
		super.bindListener(bindingMatcher, listener);
	}
}
