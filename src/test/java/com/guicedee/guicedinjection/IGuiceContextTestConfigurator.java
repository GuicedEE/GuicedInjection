package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IGuiceConfig;
import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;

public class IGuiceContextTestConfigurator
		implements IGuiceConfigurator {

	@Override
	public IGuiceConfig configure(IGuiceConfig config) {
		config.setServiceLoadWithClassPath(true);
		return config;
	}

}
