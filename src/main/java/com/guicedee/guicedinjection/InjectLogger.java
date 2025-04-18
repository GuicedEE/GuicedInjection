package com.guicedee.guicedinjection;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, CONSTRUCTOR, FIELD})
@Retention(RUNTIME)
@Documented
public @interface InjectLogger
{
    String value();
    String level() default "";

    String rollingFileName() default "";
    String fileName() default "";

}
