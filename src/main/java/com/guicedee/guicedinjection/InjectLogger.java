package com.guicedee.guicedinjection;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a Log4j {@link org.apache.logging.log4j.Logger} field for injection and
 * configures optional log naming and file output hints.
 */
@Target({METHOD, CONSTRUCTOR, FIELD})
@Retention(RUNTIME)
@Documented
public @interface InjectLogger
{
    /**
     * The logger name to use. If empty, the declaring class name is used.
     *
     * @return the logger name override
     */
    String value();

    /**
     * Optional log level hint. Not all injectors honor this value.
     *
     * @return the desired log level name
     */
    String level() default "";

    /**
     * Optional rolling file name hint for file-based appenders.
     *
     * @return the rolling file name
     */
    String rollingFileName() default "";

    /**
     * Optional file name hint for file-based appenders.
     *
     * @return the file name
     */
    String fileName() default "";

}
