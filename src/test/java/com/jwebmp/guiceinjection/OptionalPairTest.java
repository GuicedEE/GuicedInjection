package com.jwebmp.guiceinjection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionalPairTest
{
	@Test
	public void toStringTest()
	{
		Globals global = GuiceContext.getInstance(Globals.class);
	}

	@Test
	public void testOptionalPair()
	{
		OptionalPair<String, String> op = new OptionalPair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);
		Assertions.assertEquals("Key[key];Value[value]", op.toString());
	}
}
