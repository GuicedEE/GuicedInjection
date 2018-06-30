package com.jwebmp.guiceinjection;

import com.jwebmp.guiceinjection.interfaces.GuiceConfigurator;

public class GuiceContextTestConfigurator
		implements GuiceConfigurator
{
	@Override
	public GuiceConfig configure(GuiceConfig config)
	{

		return config;
	}
}
