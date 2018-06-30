package com.jwebmp.guicedinjection;

import com.jwebmp.guicedinjection.interfaces.GuiceConfigurator;

public class GuiceContextTestConfigurator
		implements GuiceConfigurator
{
	@Override
	public GuiceConfig configure(GuiceConfig config)
	{

		return config;
	}
}
