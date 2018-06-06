package com.jwebmp.guiceinjection;

import com.jwebmp.guiceinjection.interfaces.IGuiceConfigurator;

public class GuiceContextTestConfigurator
		implements IGuiceConfigurator
{
	@Override
	public GuiceConfig configure(GuiceConfig config)
	{

		return config;
	}
}
