package com.guicedee.tests;

import com.google.inject.AbstractModule;
import com.guicedee.client.IGuiceContext;
import com.guicedee.client.services.lifecycle.*;
import com.guicedee.guicedinjection.GuiceContext;
import io.smallrye.mutiny.Uni;
import io.vertx.core.*;
import java.util.*;
import java.util.concurrent.*;

/** Fresh-JVM framework runner with explicit fixture services; never starts application/database modules. */
public final class FrameworkLifecycleProbe {
    static String mode;
    static final List<String> stopped=new CopyOnWriteArrayList<>();
    static final Promise<Boolean> pending=Promise.promise();
    static boolean resourceClosed;
    public static void main(String[] args) throws Exception {
        mode=args[0];
        Class.forName("com.guicedee.client.scopes.CallScoper");var vertx=Vertx.vertx().exceptionHandler(Throwable::printStackTrace);var context=GuiceContext.instance();
        IGuiceContext.contexts.put("default",context);
        context.getConfig().setClasspathScanning(false).setServiceLoadWithClassPath(false);
        var cache=IGuiceContext.getAllLoadedServices();
        cache.put(IGuiceConfigurator.class,Set.of());cache.put(IGuicePreStartup.class,Set.of());
        cache.put(IGuiceModule.class,Set.of());
        cache.put(IGuicePostStartup.class,Set.of(new Startup()));
        cache.put(IGuicePreDestroy.class,new LinkedHashSet<>(List.of(new Late(),new SameB(),new Early(),new SameA())));
        IGuiceContext.modules.add(new AbstractModule(){protected void configure(){
            bind(Vertx.class).toInstance(vertx);
            if(mode.equals("module-failure"))addError("fixture-module-failure");
        }});
        try {
            if(mode.equals("module-failure")) {
                try {context.inject();throw new AssertionError("Failed module was accepted");}catch(RuntimeException expected){}
                requireFailed(context.getLoadingFinished());System.out.println("PROBE_OK module-failure");return;
            }
            context.inject();
            if(context.existingInjector().isEmpty())throw new AssertionError("Existing injector unavailable");
            if(mode.equals("failure") || mode.equals("subscription-failure")) {
                pending.fail("fixture-startup-failure");requireFailed(context.getLoadingFinished());
            } else {
                if(context.getLoadingFinished().isComplete())throw new AssertionError("Startup completed before hook");
                pending.complete(true);context.getLoadingFinished().toCompletionStage().toCompletableFuture().get(5,TimeUnit.SECONDS);
            }
            context.destroy();
            if(!stopped.equals(List.of("early","same-a","same-b","late")))throw new AssertionError("Wrong shutdown order: "+stopped);
            System.out.println("PROBE_OK "+mode);
            // Framework registers a JVM shutdown hook; do not execute fixture hooks twice on exit.
            cache.put(IGuicePreDestroy.class,Set.of());
        } finally {vertx.close().toCompletionStage().toCompletableFuture().get(5,TimeUnit.SECONDS);}
    }
    static void requireFailed(io.vertx.core.Future<Void> future) throws Exception {
        try {future.toCompletionStage().toCompletableFuture().get(5,TimeUnit.SECONDS);throw new AssertionError("Startup failure reported success");}
        catch(ExecutionException expected) {
            String required = switch(mode) {
                case "module-failure" -> "fixture-module-failure";
                case "subscription-failure" -> "fixture-subscription-failure";
                default -> "fixture-startup-failure";
            };
            var trace = new java.io.StringWriter();
            expected.printStackTrace(new java.io.PrintWriter(trace));
            if(!trace.toString().contains(required))throw new AssertionError("Wrong startup failure",expected);
        }
    }
    public static final class Startup implements IGuicePostStartup<Startup> {
        public List<Uni<Boolean>> postLoad(){
            if(mode.equals("subscription-failure"))return List.of(new io.smallrye.mutiny.operators.AbstractUni<Boolean>() {
                public void subscribe(io.smallrye.mutiny.subscription.UniSubscriber<? super Boolean> subscriber) {throw new IllegalStateException("fixture-subscription-failure");}
            });
            return List.of(Uni.createFrom().completionStage(pending.future().toCompletionStage()));
        }
    }
    public static final class Early implements IGuicePreDestroy<Early> {
        public Integer sortOrder(){return -100;}
        public void onDestroy(){if(resourceClosed)throw new AssertionError("Resource closed before drain");stopped.add("early");}
    }
    public static final class SameA implements IGuicePreDestroy<SameA> {
        public Integer sortOrder(){return 0;}public void onDestroy(){stopped.add("same-a");}
    }
    public static final class SameB implements IGuicePreDestroy<SameB> {
        public Integer sortOrder(){return 0;}public void onDestroy(){stopped.add("same-b");}
    }
    public static final class Late implements IGuicePreDestroy<Late> {
        public Integer sortOrder(){return -200;}public Integer shutdownSortOrder(){return 100;}public void onDestroy(){resourceClosed=true;stopped.add("late");}
    }
}
