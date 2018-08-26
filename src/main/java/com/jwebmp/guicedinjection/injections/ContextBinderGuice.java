package com.jwebmp.guicedinjection.injections;

import com.google.inject.Singleton;
import com.jwebmp.guicedinjection.GuiceConfig;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.jwebmp.guicedinjection.properties.GlobalProperties;
import com.jwebmp.logger.LogFactory;
import io.github.classgraph.ScanResult;

import java.util.logging.Logger;

/**
 * Binds the basic objects for the Guice Context to be injected everywhere
 */
@SuppressWarnings("unused")
public class ContextBinderGuice
		implements IGuiceDefaultBinder<ContextBinderGuice, GuiceInjectorModule>
{
	private static final Logger log = LogFactory.getLog("GuiceContextBinder");

	public ContextBinderGuice()
	{
		//No config required
	}

	@Override
	public void onBind(GuiceInjectorModule module)
	{
		ContextBinderGuice.log.fine("Bound GuiceConfig.class");
		module.bind(GuiceConfig.class)
		      .toProvider(() -> GuiceContext.instance()
		                                    .getConfig())
		      .in(Singleton.class);

		ContextBinderGuice.log.fine("Bound GlobalProperties.class");
		module.bind(GlobalProperties.class)
		      .asEagerSingleton();

		ContextBinderGuice.log.fine("Bound ScanResult.class");
		module.bind(ScanResult.class)
		      .toProvider(() -> GuiceContext.instance()
		                                    .getScanResult())
		      .in(Singleton.class);
	}
}
