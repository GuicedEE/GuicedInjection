package com.guicedee.guicedinjection.interfaces.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface to identify if the class can/should be aop enhanced
 * <p>
 * By default entity classes are not aop (the criteria API will not read the class as a valid entity)
 */
@Target(
		{
				ElementType.TYPE, ElementType.TYPE_USE
		})
@Retention(RetentionPolicy.RUNTIME)
public @interface INotEnhanceable {
}
