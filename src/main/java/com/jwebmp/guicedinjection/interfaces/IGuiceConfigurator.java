package com.jwebmp.guicedinjection.interfaces;

import com.jwebmp.guicedinjection.GuiceConfig;

/**
 * Service Locator Interface for granular configuration of the GuiceContext and Injector
 */
@FunctionalInterface
public interface IGuiceConfigurator
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
