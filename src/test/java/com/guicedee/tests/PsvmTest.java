package com.guicedee.tests;

import com.google.inject.Key;
import com.guicedee.client.*;
import com.guicedee.client.scopes.CallScoper;
import com.guicedee.guicedinjection.GuiceContext;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PsvmTest
{

	public static void main(String[] args)
	{
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
		IGuiceContext
				.getContext().inject();
	}

	@Test
	public void testContextPropagation() throws Exception {
		Vertx vertx = Vertx.vertx();
		CompletableFuture<Object> propagated = new CompletableFuture<>();
		try {
			vertx.getOrCreateContext().runOnContext(ignored -> {
				var context = Vertx.currentContext();
				CallScoper callScoper = IGuiceContext.get(CallScoper.class);
				try {
					callScoper.enter();
					Key<String> key = Key.get(String.class);
					callScoper.getValues().put(key, "testValue");
					Uni.createFrom().item("test")
							.onItem().delayIt().by(Duration.ofMillis(100))
							.emitOn(command -> context.runOnContext(event -> command.run()))
							.map(item -> IGuiceContext.get(CallScoper.class).getValues().get(key))
							.onTermination().invoke(callScoper::exit)
							.subscribe().with(propagated::complete, propagated::completeExceptionally);
				} catch (Throwable failure) {
					propagated.completeExceptionally(failure);
					if (callScoper.isStartedScope()) {
						callScoper.exit();
					}
				}
			});
			assertEquals("testValue", propagated.get(5, TimeUnit.SECONDS));
		} finally {
			vertx.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS);
		}
	}
}
