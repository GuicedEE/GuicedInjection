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
 * @author GedMarc
 */
public interface DefaultModuleMethods
{
    /**
     * Binds to the injector
     * @param <T>
     * @param clazz
     * @return 
     */
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz);

    /**
     * Binds to the injector
     * @param <T>
     * @param key
     * @return 
     */
    public <T> LinkedBindingBuilder<T> bind(Key<T> key);

    /**
     * Binds to the injector
     * @param <T>
     * @param typeLiteral
     * @return 
     */
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    /**
     * Binds to the injector
     * @return 
     */
    public AnnotatedConstantBindingBuilder bindConstant();

    /**
     * Binds to the injector
     * @param bindingMatcher
     * @param listener 
     */
    public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener);

    /**
     * Binds to the injector
     * @param typeMatcher
     * @param listener 
     */
    public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener);

    /**
     * Binds to the injector
     * @param scopeAnnotation
     * @param scope 
     */
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope);
}
