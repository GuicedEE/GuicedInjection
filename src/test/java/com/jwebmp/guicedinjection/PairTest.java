package com.jwebmp.guicedinjection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PairTest
{
	@Test
	public void testPair()
	{
		Pair<String, String> op = new Pair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);
		Assertions.assertEquals("Key[key]-[value}", op.toString());
	}
}
