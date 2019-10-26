package com.guicedee.guicedinjection.interfaces;

/**
 * Defines an instance as being able to be switched off if need be
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface IServiceEnablement<J extends IServiceEnablement<J>> {
	/**
	 * If this page configurator is enabled
	 *
	 * @return if the configuration must run
	 */
	boolean enabled();


}
