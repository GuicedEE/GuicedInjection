package com.jwebmp.guicedinjection.injections;

import com.google.inject.Singleton;
import com.jwebmp.guicedinjection.GuiceConfig;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.jwebmp.guicedinjection.properties.GlobalProperties;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

/**
 * Binds the basic objects for the Guice Context to be injected everywhere
 */
@SuppressWarnings("unused")
public class ContextBinderGuice
		implements IGuiceDefaultBinder<GuiceInjectorModule>
{
	public ContextBinderGuice()
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

		module.bind(GlobalProperties.class)
		      .asEagerSingleton();

		module.bind(ScanResult.class)
		      .toProvider(() -> GuiceContext.instance()
		                                    .getScanResult())
		      .in(Singleton.class);
	}
}