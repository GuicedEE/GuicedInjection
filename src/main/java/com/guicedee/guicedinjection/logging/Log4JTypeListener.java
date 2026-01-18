package com.guicedee.guicedinjection.logging;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.guicedee.guicedinjection.InjectLogger;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * Guice type listener that registers {@link Log4JMembersInjector} for fields
 * annotated with {@link InjectLogger}.
 */
public class Log4JTypeListener implements TypeListener {
    /**
     * Scans type fields for {@link Logger} targets annotated with {@link InjectLogger}.
     *
     * @param typeLiteral the type being inspected
     * @param typeEncounter the encounter used to register injectors
     * @param <T> the type being inspected
     */
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        Class<?> clazz = typeLiteral.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class &&
                        field.isAnnotationPresent(InjectLogger.class)) {
                    typeEncounter.register(new Log4JMembersInjector<T>(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
