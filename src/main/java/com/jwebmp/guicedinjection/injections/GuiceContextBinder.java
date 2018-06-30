package com.jwebmp.guicedinjection.injections;

import com.google.inject.Singleton;
import com.jwebmp.guicedinjection.Globals;
import com.jwebmp.guicedinjection.GuiceConfig;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.interfaces.GuiceDefaultBinder;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

/**
 * Binds the basic objects for the Guice Context to be injected everywhere
 */
@SuppressWarnings("unused")
public class GuiceContextBinder
		extends GuiceDefaultBinder
{
	public GuiceContextBinder()
	{
		//No config required
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.bind(GuiceConfig.class)
		      .toProvider(() -> GuiceContext.instance()
		                                    .getConfig())
		      .in(Singleton.class);
		module.bind(Globals.class)
		      .asEagerSingleton();
		module.bind(ScanResult.class)
		      .toProvider(() -> GuiceContext.instance()
		                                    .getScanResult())
		      .in(Singleton.class);
	}
}
