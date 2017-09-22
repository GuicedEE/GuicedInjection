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
import javax.cache.annotation.CacheResult;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Facade Method implementer for the change from the org.Reflections library to the FastClasspathScanner
 * <p>
 * TODO This one
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
	 * @param <T>  The type to check
	 * @param type variable
	 *
	 * @return A set of classes matching
	 */
	@CacheResult
	public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type)
	{
		Set<Class<? extends T>> returnable = new HashSet<>();
		List<String> allClasses = GuiceContext.context().getScanResult().getNamesOfAllClasses();
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
	public <T> Set<Class<? extends T>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation)
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
	
	@CacheResult
	public Optional<Field> getFieldAnnotatedWithOfType(Class<? extends Annotation> annotation, Class type, Class in)
	{
		Field field = null;
		Class inType = in;
		Field[] allFields = inType.getFields();
		for (Field f : allFields)
		{
			if (f.getAnnotationsByType(annotation) != null)
			{
				if (f.getAnnotationsByType(annotation).length > 0)
				{
					if (f.getType().equals(type))
					{
						field = f;
						break;
					}
				}
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
	public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation)
	{
		
		return null;
	}
	
	/**
	 * Gets any methods with a parameter associated on it
	 *
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> annotation)
	{
		return null;
	}
	
	/**
	 * Gets any methods with the annotation attached
	 *
	 * @param annotation
	 *
	 * @return
	 */
	@CacheResult
	public Set<Method> getMethodsWithAnyParamAnnotated(Annotation annotation)
	{
		return null;
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
	public <T> Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation)
	{
		return null;
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
	public Set<String> getResources(final Predicate<String> namePredicate)
	{
		return null;
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
	public Set<String> getResources(final Pattern pattern)
	{
		return null;
	}
	
	/**
	 * Returns all the members that access a particular field
	 *
	 * @param field
	 *
	 * @return
	 */
	@CacheResult
	public Set<Member> getFieldUsage(Field field)
	{
		return null;
	}
	
}
