package com.jwebmp.guiceinjection.injections;

import com.google.inject.Singleton;
import com.jwebmp.guiceinjection.Globals;
import com.jwebmp.guiceinjection.GuiceConfig;
import com.jwebmp.guiceinjection.GuiceContext;
import com.jwebmp.guiceinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guiceinjection.interfaces.GuiceDefaultBinder;
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
