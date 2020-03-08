package com.guicedee.guicedinjection.interfaces.annotations;

import java.lang.annotation.*;

/**
 * Marker interface to identify if the class is not injectable
 */

@Target(
		{
				ElementType.TYPE, ElementType.TYPE_USE
		})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface INotInjectable {
}
