package za.co.mmagon.guiceinjection;

import za.co.mmagon.guiceinjection.interfaces.IGuiceConfigurator;

public class GuiceContextTestConfigurator
		implements IGuiceConfigurator
{
	@Override
	public GuiceConfig configure(GuiceConfig config)
	{

		return config;
	}
}
