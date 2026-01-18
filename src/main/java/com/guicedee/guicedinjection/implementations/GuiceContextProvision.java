package com.guicedee.guicedinjection.implementations;

import com.guicedee.client.*;
import com.guicedee.client.services.IGuiceProvider;
import com.guicedee.guicedinjection.*;

/**
 * Provides the shared {@link GuiceContext} instance via the {@link IGuiceProvider} SPI.
 */
public class GuiceContextProvision implements IGuiceProvider
{
	/**
	 * Returns the singleton {@link GuiceContext} instance.
	 *
	 * @return the shared Guice context
	 */
	@Override
	public IGuiceContext get()
	{
		return GuiceContext.instance();
	}
	
}
