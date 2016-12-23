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

    public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz);

    public <T> LinkedBindingBuilder<T> bind(Key<T> key);

    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    public AnnotatedConstantBindingBuilder bindConstant();

    public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener);

    public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener);

    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope);
}
