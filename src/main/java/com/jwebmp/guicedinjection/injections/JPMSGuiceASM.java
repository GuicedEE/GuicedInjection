package com.jwebmp.guicedinjection.injections;

import com.jwebmp.guicedinjection.interfaces.IGuicePreStartup;
import com.jwebmp.logger.LogFactory;

public class JPMSGuiceASM
		implements IGuicePreStartup
{
	@Override
	public void onStartup()
	{
		LogFactory.getLog("JPMSGuiceASM")
		          .config("Configuring Guice with $experimental_asm7 to true");
		System.setProperty("com.google.inject.internal.cglib.$experimental_asm7", "true");
	}
}
