package com.guicedee.guicedinjection.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.guicedee.guicedinjection.GuiceConfig;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.JobService;
import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.guicedee.guicedinjection.logging.Log4JTypeListener;
import com.guicedee.client.utils.GlobalProperties;
import io.github.classgraph.ScanResult;
import lombok.extern.log4j.Log4j2;

/**
 * Guice module that binds core GuiceContext services for injection.
 */
@SuppressWarnings("unused")
@Log4j2
public class ContextBinderGuice
        extends AbstractModule
        implements IGuiceModule<ContextBinderGuice> {
    public ContextBinderGuice() {
        //No config required
    }

    /**
     * Configures core bindings including {@link GuiceConfig}, {@link GlobalProperties},
     * and the active {@link ScanResult}.
     */
    @Override
    public void configure() {
        ContextBinderGuice.log.debug("Bound GuiceConfig.class");
        bind(GuiceConfig.class)
                .toProvider(() -> GuiceContext.instance().getConfig());

        ContextBinderGuice.log.debug("Bound GlobalProperties.class");
        bind(GlobalProperties.class)
                .asEagerSingleton();

        ContextBinderGuice.log.debug("Bound ScanResult.class");
        bind(ScanResult.class)
                .toProvider(() -> GuiceContext.instance()
                        .getScanResult())
                .in(Singleton.class);

        bindListener(Matchers.any(), new Log4JTypeListener());
    }
}
