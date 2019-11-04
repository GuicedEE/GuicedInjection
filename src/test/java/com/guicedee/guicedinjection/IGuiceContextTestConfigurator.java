package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;

public class IGuiceContextTestConfigurator
		implements IGuiceConfigurator {

	@Override
	public GuiceConfig configure(GuiceConfig config) {
		config.setServiceLoadWithClassPath(true);
		return config;
	}

}
