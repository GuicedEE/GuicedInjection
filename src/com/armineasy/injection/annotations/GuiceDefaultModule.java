package com.armineasy.injection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author GedMarc
 */
@Target(
        {
            ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE
        })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GuiceDefaultModule
{
    
}
