package com.jwebmp.guicedinjection;

import com.jwebmp.guicedinjection.interfaces.IGuiceConfigurator;

public class IGuiceContextTestConfigurator
		implements IGuiceConfigurator
{
	@Override
	public GuiceConfig configure(GuiceConfig config)
	{

		return config;
	}
}
