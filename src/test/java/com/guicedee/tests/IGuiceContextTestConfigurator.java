package com.guicedee.tests;

import com.guicedee.client.services.IGuiceConfig;
import com.guicedee.client.services.lifecycle.IGuiceConfigurator;

public class IGuiceContextTestConfigurator
		implements IGuiceConfigurator<IGuiceContextTestConfigurator> {

	@Override
	public IGuiceConfig configure(IGuiceConfig config) {
		config.setServiceLoadWithClassPath(true);
		return config;
	}

}
