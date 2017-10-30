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

import com.google.common.base.Predicate;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Facade Method implementer for the change from the org.Reflections library to the FastClasspathScanner
 * <p>
 *
 * @author Marc Magon
 * @since 07 Jul 2017
 */
@CacheDefaults(cacheName = "Reflections")
public class Reflections
{

	/*
	 * Constructs a new Reflections
	 */
	public Reflections()
	{
		//Nothing needed
	}

	/**
	 * Returns all the subtypes (interface or abstract) of a given class type
	 *
	 * @param <T>
	 * 		The type to check
	 * @param type
	 * 		variable
	 *
	 * @return A set of classes matching
	 */
	@CacheResult
	@NotNull
	public <T> Set<Class<? extends T>> getSubTypesOf(@CacheKey final Class<T> type)
	{
		Set<Class<? extends T>> returnable = new HashSet<>();
		List<String> subtypes = type.isInterface() ? GuiceContext.context().getScanResult().getNamesOfClassesImplementing(type) : GuiceContext.context().getScanResult().getNamesOfSubclassesOf(type);
		for (String subtype : subtypes)
		{
			Class<T> subType = (Class<T>) GuiceContext.context().getScanResult().classNameToClassRef(subtype);
			returnable.add(subType);
		}
		return returnable;
	}

	/**
	 * Returns all the class types annotated with an annotation
	 *
	 * @param <T>
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public <T> Set<Class<? extends T>> getTypesAnnotatedWith(@CacheKey final Class<? extends Annotation> annotation)
	{
		Set<Class<? extends T>> returnable = new HashSet<>();
		List<String> subtypes = GuiceContext.context().getScanResult().getNamesOfClassesWithAnnotation(annotation);
		for (String subtype : subtypes)
		{
			Class<T> subType = (Class<T>) GuiceContext.context().getScanResult().classNameToClassRef(subtype);
			returnable.add(subType);
		}
		return returnable;
	}

	/**
	 * Get all fields with the annotation
	 *
	 * @param annotation
	 * @param type
	 * @param in
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Optional<Field> getFieldAnnotatedWithOfType(@CacheKey Class<? extends Annotation> annotation, Class type, Class in)
	{
		Field field = null;
		Class inType = in;
		Field[] allFields = inType.getFields();
		for (Field f : allFields)
		{

			if (f.getAnnotationsByType(annotation) != null && f.getAnnotationsByType(annotation).length > 0 && f.getType().equals(type))
			{
				field = f;
				break;
			}
		}
		return Optional.ofNullable(field);
	}

	/**
	 * Returns all the methods annotated with an annotation
	 *
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<Method> getMethodsAnnotatedWith(@CacheKey final Class<? extends Annotation> annotation)
	{
		return Collections.emptySet();
	}

	/**
	 * Gets any methods with a parameter associated on it
	 *
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<Method> getMethodsWithAnyParamAnnotated(@CacheKey Class<? extends Annotation> annotation)
	{
		return Collections.emptySet();
	}

	/**
	 * Gets any methods with the annotation attached
	 *
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<Method> getMethodsWithAnyParamAnnotated(@CacheKey Annotation annotation)
	{
		return Collections.emptySet();
	}

	/**
	 * Gets all the fields annotated with an annotation
	 *
	 * @param <T>
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<Field> getFieldsAnnotatedWith(@CacheKey final Class<? extends Annotation> annotation)
	{
		return Collections.emptySet();
	}

	/**
	 * Gets all the resources with a given name predicate.
	 * Operates in its own scanner, may be a little slower
	 *
	 * @param namePredicate
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<String> getResources(@CacheKey final Predicate<String> namePredicate)
	{
		return Collections.emptySet();
	}

	/**
	 * Gets all the resources with a given pattern
	 * * Operates in its own scanner, may be a little slower
	 *
	 * @param pattern
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<String> getResources(@CacheKey final Pattern pattern)
	{
		return Collections.emptySet();
	}

	/**
	 * Returns all the members that access a particular field
	 *
	 * @param field
	 *
	 * @return
	 */
	@CacheResult
	@NotNull
	public Set<Member> getFieldUsage(@CacheKey Field field)
	{
		return Collections.emptySet();
	}

}
