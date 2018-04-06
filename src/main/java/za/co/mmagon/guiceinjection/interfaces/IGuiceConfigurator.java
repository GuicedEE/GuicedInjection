package za.co.mmagon.guiceinjection.interfaces;

import za.co.mmagon.guiceinjection.GuiceConfig;

import java.io.Serializable;

/**
 * Service Locator Interface for granular configuration of the GuiceContext and Injector
 */
@FunctionalInterface
public interface IGuiceConfigurator
		extends Serializable
{
	/**
	 * Configuers the guice instance
	 *
	 * @param config
	 *
	 * @return
	 */
	GuiceConfig configure(GuiceConfig config);
}
