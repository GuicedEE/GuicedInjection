package com.jwebmp.guiceinjection.interfaces;

import com.jwebmp.guiceinjection.GuiceConfig;

import java.io.Serializable;

/**
 * Service Locator Interface for granular configuration of the GuiceContext and Injector
 */
@FunctionalInterface
public interface GuiceConfigurator
		extends Serializable
{
	/**
	 * Configuers the guice instance
	 *
	 * @param config
	 * 		The configuration object coming in
	 *
	 * @return The required guice configuration
	 */
	GuiceConfig configure(GuiceConfig config);
}
