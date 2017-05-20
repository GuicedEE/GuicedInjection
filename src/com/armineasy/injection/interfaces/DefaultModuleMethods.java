/*
 * The MIT License
 *
 * Copyright 2017 Marc Magon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz);

    /**
     * Binds to the injector
     *
     * @param <T>
     * @param key
     *
     * @return
     */
    public <T> LinkedBindingBuilder<T> bind(Key<T> key);

    /**
     * Binds to the injector
     *
     * @param <T>
     * @param typeLiteral
     *
     * @return
     */
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    /**
     * Binds to the injector
     *
     * @return
     */
    public AnnotatedConstantBindingBuilder bindConstant();

    /**
     * Binds to the injector
     *
     * @param bindingMatcher
     * @param listener
     */
    public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener);

    /**
     * Binds to the injector
     *
     * @param typeMatcher
     * @param listener
     */
    public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener);

    /**
     * Binds to the injector
     *
     * @param scopeAnnotation
     * @param scope
     */
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope);
}
