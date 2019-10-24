package com.guicedee.guicedinjection.injections;

import com.google.inject.Singleton;
import com.guicedee.guicedinjection.GuiceConfig;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
import com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.guicedee.guicedinjection.interfaces.JobService;
import com.guicedee.guicedinjection.properties.GlobalProperties;
import io.github.classgraph.ScanResult;
import com.guicedee.logger.LogFactory;

import java.util.logging.Logger;

/**
 * Binds the basic objects for the Guice Context to be injected everywhere
 */
@SuppressWarnings("unused")
public class ContextBinderGuice
		implements IGuiceDefaultBinder<ContextBinderGuice, GuiceInjectorModule> {
	private static final Logger log = LogFactory.getLog("GuiceContextBinder");

	public ContextBinderGuice() {
		//No config required
	}

	@Override
	public void onBind(GuiceInjectorModule module) {
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

		ContextBinderGuice.log.fine("Bound JobService.class");
		module.bind(JobService.class)
			  .asEagerSingleton();
	}


}
