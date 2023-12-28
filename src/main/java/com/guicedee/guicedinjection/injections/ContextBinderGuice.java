package com.guicedee.guicedinjection.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.guicedee.guicedinjection.GuiceConfig;
import com.guicedee.guicedinjection.GuiceContext;

import com.guicedee.guicedinjection.JobService;
import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.guicedee.guicedinjection.properties.GlobalProperties;
import io.github.classgraph.ScanResult;
import lombok.extern.java.Log;


import java.util.logging.Logger;

/**
 * Binds the basic objects for the Guice Context to be injected everywhere
 */
@SuppressWarnings("unused")
@Log
public class ContextBinderGuice
        extends AbstractModule
        implements IGuiceModule<ContextBinderGuice> {
    public ContextBinderGuice() {
        //No config required
    }

    @Override
    public void configure() {
        ContextBinderGuice.log.fine("Bound GuiceConfig.class");
        bind(GuiceConfig.class)
                .toProvider(() -> GuiceContext.instance().getConfig());

        ContextBinderGuice.log.fine("Bound GlobalProperties.class");
        bind(GlobalProperties.class)
                .asEagerSingleton();

        ContextBinderGuice.log.fine("Bound ScanResult.class");
        bind(ScanResult.class)
                .toProvider(() -> GuiceContext.instance()
                        .getScanResult())
                .in(Singleton.class);

        ContextBinderGuice.log.fine("Bound JobService.class");
        bind(JobService.class)
                .toInstance(JobService.getInstance());
    }
}
