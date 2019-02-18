/*
 * Copyright (C) 2017 GedMarc
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
package com.jwebmp.guicedinjection;

import io.github.classgraph.ClassInfoList;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Facade Method implementer for the change from the org.Reflections library to the FastClasspathScanner
 * <p>
 *
 * @author GedMarc
 * @since 07 Jul 2017
 */
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
	@NotNull
	@SuppressWarnings("unchecked")
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type)
	{
		ClassInfoList subtypes = GuiceContext.instance()
		                                     .getScanResult()
		                                     .getSubclasses(type.getCanonicalName());
		return new HashSet(subtypes.loadClasses());
	}

	/**
	 * Get all fields with the annotation
	 *
	 * @param annotation
	 * 		The type of annotations to find within a given class
	 * @param type
	 * 		The type to find
	 * @param in
	 * 		The class to look in
	 *
	 * @return A given field that can be used on an object. Set Accessible is not run.
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "unused"})
	public Optional<Field> getFieldAnnotatedWithOfType(Class<? extends Annotation> annotation, Class type, Class in)
	{
		Field field = null;
		Field[] allFields = in.getFields();
		for (Field f : allFields)
		{
			if (f.getAnnotationsByType(annotation) != null && f.getAnnotationsByType(annotation).length > 0 && f.getType()
			                                                                                                    .equals(type))
			{
				field = f;
				break;
			}
		}
		return Optional.ofNullable(field);
	}
}
