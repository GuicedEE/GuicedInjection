package com.guicedee.guicedinjection;

import com.guicedee.client.Environment;
import com.guicedee.client.IGuiceContext;
import org.apache.logging.log4j.LogManager;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/** Process entry boundary. Returns after startup; failed startup cleans up and exits unsuccessfully. */
public final class GuiceApplication {
    private GuiceApplication() {}

    /**
     * Runs preparation and injection within GUICEDEE_STARTUP_TIMEOUT_SECONDS (default 300).
     * Failure has GUICEDEE_FAILED_STARTUP_CLEANUP_SECONDS (default 45) to stop the process,
     * including cleanup and JVM shutdown hooks. This method is for main entry points only.
     */
    public static void run(Runnable preparation) {
        Objects.requireNonNull(preparation);
        var context = new AtomicReference<IGuiceContext>();
        var phase = new AtomicReference<>("deadline configuration");
        Thread startup = null;
        int cleanupSeconds = 45;
        boolean interrupted = false;
        try {
            cleanupSeconds = seconds("GUICEDEE_FAILED_STARTUP_CLEANUP_SECONDS", 45, 120);
            int startupSeconds = seconds("GUICEDEE_STARTUP_TIMEOUT_SECONDS", 300, 1800);
            var ready = new CompletableFuture<Void>();
            // Preserve normal non-daemon inheritance for resources created by startup hooks.
            startup = Thread.ofPlatform().daemon(false).name("guicedee-application-startup").start(() -> {
                try {
                    phase.set("context discovery");
                    var active = IGuiceContext.instance();
                    if (active instanceof GuiceContext guice) guice.manageProcessLifecycle();
                    context.set(active);
                    phase.set("application preparation");
                    preparation.run();
                    phase.set("injector initialization");
                    active.inject();
                    phase.set("asynchronous startup");
                    active.getLoadingFinished().onComplete(result -> {
                        if (result.succeeded()) ready.complete(null);
                        else ready.completeExceptionally(result.cause());
                    });
                } catch (Throwable failed) {
                    ready.completeExceptionally(failed);
                }
            });
            ready.get(startupSeconds, TimeUnit.SECONDS);
            return;
        } catch (InterruptedException cancelled) {
            interrupted = true;
            phase.set("interrupted startup");
        } catch (TimeoutException expired) {
            phase.set("startup deadline exceeded");
        } catch (Throwable failed) {
            // Retain the fixed phase without repeating configuration values or raw causes.
        }

        // A stuck cleanup hook or JVM shutdown hook must not leave a failed process serving traffic.
        final int cleanupBudget = cleanupSeconds;
        Thread.ofPlatform().daemon().name("guicedee-failed-startup-deadline").start(() -> {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(cleanupBudget);
            while (System.nanoTime() < deadline) {
                try {
                    TimeUnit.NANOSECONDS.sleep(Math.max(1, deadline - System.nanoTime()));
                } catch (InterruptedException ignored) {
                    // Preserve the absolute failure deadline.
                }
            }
            Runtime.getRuntime().halt(1);
        });
        LogManager.getLogger(GuiceApplication.class).error("Application startup failed during {}; shutting down", phase.get());
        if (startup != null) startup.interrupt();
        try {
            var active = context.get();
            if (active != null) active.destroy();
        } catch (Throwable failedCleanup) {
            LogManager.getLogger(GuiceApplication.class).error("Application startup cleanup failed");
        } finally {
            if (interrupted) Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    private static int seconds(String name, int fallback, int maximum) {
        try {
            String value = Environment.getSystemPropertyOrEnvironment(name, Integer.toString(fallback));
            if (!value.matches("[1-9][0-9]{0,3}")) throw new IllegalArgumentException();
            int seconds = Integer.parseInt(value);
            if (seconds > maximum) throw new IllegalArgumentException();
            return seconds;
        } catch (Exception invalid) {
            throw new IllegalArgumentException("Invalid application startup deadline");
        }
    }
}
