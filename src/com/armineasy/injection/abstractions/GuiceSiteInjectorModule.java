package com.armineasy.injection.abstractions;

import com.armineasy.injection.GuiceContext;
import com.armineasy.injection.interfaces.DefaultModuleMethods;
import com.armineasy.injection.interfaces.GuiceSiteBinder;
import com.google.inject.*;
import com.google.inject.binder.*;
import com.google.inject.matcher.Matcher;
import com.google.inject.servlet.ServletModule;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 * Loads up all the Guice Servlet Binders
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public class GuiceSiteInjectorModule extends ServletModule implements DefaultModuleMethods
{

    private static final Logger log = Logger.getLogger("GuiceSiteInjectorModule");

    public GuiceSiteInjectorModule()
    {
        //Nothing needed
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     *
     * @param urlPattern
     * @param morePatterns
     * @return
     */
    public ServletKeyBindingBuilder serveSite(String urlPattern, String... morePatterns)
    {
        return serve(urlPattern, morePatterns);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param urlPatterns
     * @return 
     */
    public ServletKeyBindingBuilder serveSite(Iterable<String> urlPatterns)
    {
        return serve(urlPatterns);
    }

    /**
     * regexes - Any Java-style regular expressions. Since: 4.1
     * @param regex
     * @param regexes
     * @return 
     */
    public ServletKeyBindingBuilder serveSiteRegex(String regex, String... regexes)
    {
        return serveRegex(regex, regexes);
    }

    /**
     * regexes - Any Java-style regular expressions. Since: 4.1
     * @param regexes
     * @return 
     */
    public ServletKeyBindingBuilder serveSiteRegex(Iterable<String> regexes)
    {
        return serveRegex(regexes);
    }

    /**
     * Runs the binders for the system
     */
    public void runBinders()
    {
        Reflections reflections = GuiceContext.reflect();
        Set<Class<? extends GuiceSiteBinder>> siteBinders = reflections.getSubTypesOf(GuiceSiteBinder.class);
        log.log(Level.CONFIG, "Total number of site injectors - {0}", siteBinders.size());
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
                log.log(Level.SEVERE, "Couldn;t load module from sets" + siteBinders.toString(), ex);
            }
        });
        if (!objects.isEmpty())
        {
            objects.stream().forEachOrdered(obj ->
            {
                log.log(Level.CONFIG, "Loading Guice Servlet Configuration {0}", obj.getClass().getSimpleName());
                obj.onBind(this);
                log.log(Level.CONFIG, "Loaded Guice Servlet Configuration {0}", obj.getClass().getSimpleName());
            });
        }
    }

    /**
     * Runs the binders
     */
    @Override
    protected void configureServlets()
    {
        runBinders();
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz)
    {
        return super.bind(clazz);
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(Key<T> key)
    {
        return super.bind(key);
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral)
    {
        return super.bind(typeLiteral);
    }

    @Override
    public AnnotatedConstantBindingBuilder bindConstant()
    {
        return super.bindConstant();
    }

    @Override
    public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listener)
    {
        super.bindListener(bindingMatcher, listener);
    }

    @Override
    public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener)
    {
        super.bindListener(typeMatcher, listener);
    }

    @Override
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope)
    {
        super.bindScope(scopeAnnotation, scope);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param urlPatterns
     * @return 
     */
    public FilterKeyBindingBuilder filter$(Iterable<String> urlPatterns)
    {
        return super.filter(urlPatterns);
    }

    /**
     * regexes - Any Java-style regular expressions. Since: 4.1
     * @param regex
     * @param regexes
     * @return 
     */
    public FilterKeyBindingBuilder filterRegex$(String regex, String... regexes)
    {
        return super.filterRegex(regex, regexes);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param regexes
     * @return 
     */
    public FilterKeyBindingBuilder filterRegex$(Iterable<String> regexes)
    {
        return super.filterRegex(regexes);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param urlPattern
     * @param morePatterns
     * @return 
     */
    public ServletKeyBindingBuilder serve$(String urlPattern, String... morePatterns)
    {
        return super.serve(urlPattern, morePatterns);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param urlPatterns
     * @return 
     */
    public ServletKeyBindingBuilder serve$(Iterable<String> urlPatterns)
    {
        return super.serve(urlPatterns);
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param regex
     * @param regexes
     * @return 
     */
    public ServletKeyBindingBuilder serveRegex$(String regex, String... regexes)
    {
        return super.serveRegex(regex, regexes);
    }

    /**
     * regexes - Any Java-style regular expressions. Since: 4.1
     * @param regexes
     * @return 
     */
    public ServletKeyBindingBuilder serveRegex$(Iterable<String> regexes)
    {
        return super.serveRegex(regexes);
    }

    /**
     * This method only works if you are using the GuiceServletContextListener to create your injector. Otherwise, it returns null.
     * @return 
     */
    public javax.servlet.ServletContext getServletContext$()
    {
        return super.getServletContext();
    }

    /**
     * urlPatterns - Any Servlet-style patterns. examples: /*, /html/*, *.html, etc. Since: 4.1
     * @param urlPattern
     * @param morePatterns
     * @return 
     */
    public FilterKeyBindingBuilder filter$(String urlPattern, String... morePatterns)
    {
        return super.filter(urlPattern, morePatterns);
    }

}
