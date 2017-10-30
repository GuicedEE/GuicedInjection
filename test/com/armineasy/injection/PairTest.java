package com.armineasy.injection;

import org.junit.jupiter.api.Test;

class PairTest
{
	@Test
	public void testPair()
	{
		Pair<String, String> op = new Pair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);
	}
}
