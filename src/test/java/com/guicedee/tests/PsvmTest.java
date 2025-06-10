package com.guicedee.tests;

import com.google.inject.Key;
import com.guicedee.client.*;
import com.guicedee.guicedinjection.GuiceContext;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import java.time.Duration;

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
	public void testContextPropagation() {
		// Set some value in call scope
		CallScoper callScoper = IGuiceContext.get(CallScoper.class);
		callScoper.enter();
		Key<String> key = Key.get(String.class);
		callScoper.getValues().put(key, "testValue");

		// Create a Uni that should use the custom executor
		Uni.createFrom().item("test")
				.onItem().delayIt().by(Duration.ofMillis(100))
				.subscribe().with(
						item -> {
							// Check if the call scope value is available here
							CallScoper newCallScoper = IGuiceContext.get(CallScoper.class);
							Object value = newCallScoper.getValues().get(key);
							System.out.println("Call scope value in new thread: " + value);
						}
				);
	}
}
