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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Facade Method implementer for the change from the org.Reflections library to the FastClasspathScanner
 *
 * @author Marc Magon
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

    public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type)
    {
        Set<Class<? extends T>> returnable = new HashSet<>();
        List<String> allClasses = GuiceContext.context().scanResult.getNamesOfAllClasses();
        if (!allClasses.contains("com.armineasy.lsm.LSMSiderBar"))
        {
            System.out.println("No this is there!");
        }
        List<String> subtypes = type.isInterface() ? GuiceContext.context().scanResult.getNamesOfClassesImplementing(type) : GuiceContext.context().scanResult.getNamesOfSubclassesOf(type);
        subtypes.stream().map(subtype -> (Class<T>) GuiceContext.context().scanResult.classNameToClassRef(subtype)).forEach(clazz ->
        {
            returnable.add(clazz);
        });
        return returnable;
    }

    public <T> Set<Class<? extends T>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation)
    {
        Set<Class<? extends T>> returnable = new HashSet<>();
        List<String> subtypes = GuiceContext.context().scanResult.getNamesOfClassesWithAnnotation(annotation);
        subtypes.stream().map(subtype -> (Class<T>) GuiceContext.context().scanResult.classNameToClassRef(subtype)).forEach(clazz ->
        {
            returnable.add(clazz);
        });
        return returnable;
    }

    public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation)
    {

        return null;
    }

    public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> annotation)
    {
        return null;
    }

    public Set<Method> getMethodsWithAnyParamAnnotated(Annotation annotation)
    {
        return null;
    }

    public <T> Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation)
    {
        return null;
    }

    public Set<String> getResources(final Predicate<String> namePredicate)
    {
        return null;
    }

    public Set<String> getResources(final Pattern pattern)
    {
        return null;
    }

    public Set<Member> getFieldUsage(Field field)
    {
        return null;
    }

}
