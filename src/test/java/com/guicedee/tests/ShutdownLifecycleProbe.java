package com.guicedee.tests;

import com.google.inject.AbstractModule;
import com.guicedee.client.IGuiceContext;
import com.guicedee.client.services.lifecycle.*;
import com.guicedee.guicedinjection.GuiceContext;
import io.vertx.core.Vertx;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Direct-injection process fixture: includes the real JVM shutdown hook. */
public final class ShutdownLifecycleProbe {
    static String mode;
    static final AtomicInteger closed = new AtomicInteger();
    static final CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
    static final Object resource = new Object();
    static volatile boolean dependencyResolved;

    public static void main(String[] args) throws Exception {
        mode = args[0];
        Class.forName("com.guicedee.client.scopes.CallScoper");
        var vertx = Vertx.vertx();
        var context = GuiceContext.instance();
        IGuiceContext.contexts.put("default", context);
        context.getConfig().setClasspathScanning(false).setServiceLoadWithClassPath(false);
        var services = IGuiceContext.getAllLoadedServices();
        services.put(IGuiceConfigurator.class, Set.of());
        services.put(IGuicePreStartup.class, Set.of());
        services.put(IGuiceModule.class, Set.of());
        services.put(IGuicePostStartup.class, Set.of());
        services.put(IGuicePreDestroy.class, Set.of(new Cleanup()));
        IGuiceContext.modules.add(new AbstractModule() {
            protected void configure() {
                bind(Vertx.class).toInstance(vertx);
                bind(Object.class).toInstance(resource);
                if (mode.equals("startup-failure")) addError("fixture-startup-failure");
            }
        });
        try {
            if (mode.equals("startup-failure")) {
                try { context.inject(); throw new AssertionError("Startup failure accepted"); }
                catch (RuntimeException expected) { }
            } else {
                context.inject();
                context.getLoadingFinished().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS);
            }
            if (mode.equals("concurrent")) {
                var first = CompletableFuture.runAsync(context::destroy);
                if (!entered.await(5, TimeUnit.SECONDS)) throw new AssertionError("Cleanup never entered");
                var second = CompletableFuture.runAsync(context::destroy);
                release.countDown();
                first.get(5, TimeUnit.SECONDS);
                second.get(5, TimeUnit.SECONDS);
            } else context.destroy();
            if (!mode.equals("startup-failure") && !dependencyResolved)
                throw new AssertionError("Cleanup could not resolve its running dependency");

            // Recreate discovery input to detect cleanup reloads after services were cleared.
            services.put(IGuicePreDestroy.class, Set.of(new Cleanup()));
            context.destroy();
            if (closed.get() != 1) throw new AssertionError("Cleanup repeated: " + closed.get());
            if (context.existingInjector().isPresent()) throw new AssertionError("Injector retained after shutdown");
            try { IGuiceContext.get(Object.class); throw new AssertionError("Stopped injector restarted"); }
            catch (IllegalStateException expected) { }
            if (closed.get() != 1) throw new AssertionError("Injection triggered another cleanup");
            System.out.println("PROBE_OK " + mode);
            // Do not remove the hook or its fixture: natural JVM exit must also be harmless.
        } finally {
            release.countDown();
            vertx.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS);
        }
    }

    public static final class Cleanup implements IGuicePreDestroy<Cleanup> {
        public void onDestroy() {
            System.out.println("CLEANUP");
            closed.incrementAndGet();
            if (GuiceContext.instance().existingInjector().isPresent()) {
                if (IGuiceContext.get(Object.class) != resource)
                    throw new AssertionError("Cleanup lost its existing dependency");
                dependencyResolved = true;
            }
            if (mode.equals("reentrant")) GuiceContext.instance().destroy();
            if (mode.equals("concurrent")) {
                entered.countDown();
                try {
                    if (!release.await(5, TimeUnit.SECONDS)) throw new AssertionError("Cleanup release timed out");
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(interrupted);
                }
            }
            if (mode.equals("cleanup-failure")) throw new IllegalStateException("fixture-cleanup-failure");
        }
    }
}
