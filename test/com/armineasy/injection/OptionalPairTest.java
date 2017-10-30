package com.armineasy.injection;

import org.junit.jupiter.api.Test;

class OptionalPairTest
{

	@Test
	public void testOptionalPair()
	{
		OptionalPair<String, String> op = new OptionalPair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);

	}
}
