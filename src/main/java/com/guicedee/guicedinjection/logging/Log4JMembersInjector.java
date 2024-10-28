package com.guicedee.guicedinjection.logging;

import com.google.common.base.Strings;
import com.google.inject.MembersInjector;
import com.guicedee.guicedinjection.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class Log4JMembersInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final InjectLogger injectLogger;
    private final Logger logger;

    Log4JMembersInjector(Field field) {
        this.field = field;
        this.injectLogger = field.getAnnotation(InjectLogger.class);
        String logName = Strings.isNullOrEmpty(injectLogger.value()) ? field.getDeclaringClass().getCanonicalName() : injectLogger.value();
        this.logger = LogManager.getLogger(logName);
        field.setAccessible(true);
    }

    public void injectMembers(T t) {
        try {
            field.set(t, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
