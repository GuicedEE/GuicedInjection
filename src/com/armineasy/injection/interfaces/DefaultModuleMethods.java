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
package com.armineasy.injection.interfaces;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;

/**
 * The duplicated lines in the modules but the abstract hierarchy must be kept
 *
 * @author GedMarc
 */
public interface DefaultModuleMethods
{
	
	/**
	 * Binds to the injector
	 *
	 * @param <T>
	 * @param clazz
	 *
	 * @return
	 */
	<T> AnnotatedBindingBuilder<T> bind(Class<T> clazz);
	
	/**
	 * Binds to the injector
	 *
	 * @param <T>
	 * @param key
	 *
	 * @return
	 */
	<T> LinkedBindingBuilder<T> bind(Key<T> key);
	
	/**
	 * Binds to the injector
	 *
	 * @param <T>
	 * @param typeLiteral
	 *
	 * @return
	 */
	<T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);
	
	/**
	 * Binds to the injector
	 *
	 * @return
	 */
	AnnotatedConstantBindingBuilder bindConstant();
	
	/**
	 * Binds to the injector
	 *
	 * @param bindingMatcher
	 * @param listener
	 */
	void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener);
	
	/**
	 * Binds to the injector
	 *
	 * @param typeMatcher
	 * @param listener
	 */
	void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener);
	
	/**
	 * Binds to the injector
	 *
	 * @param scopeAnnotation
	 * @param scope
	 */
	void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope);
}
