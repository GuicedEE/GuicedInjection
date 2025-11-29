package com.guicedee.tests;

import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.client.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PairTest {
	@Test
	public void testPair() {
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
		Pair<String, String> op = new Pair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);
		Assertions.assertEquals("Key[key]-[value}", op.toString());
	}


}
