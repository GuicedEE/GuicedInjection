package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.pairing.OptionalPair;
import com.guicedee.guicedinjection.properties.GlobalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionalPairTest {
	@Test
	public void toStringTest() {
		GlobalProperties global = GuiceContext.get(GlobalProperties.class);
	}

	@Test
	public void testOptionalPair() {
		OptionalPair<String, String> op = new OptionalPair<>();
		op.setKey("key");
		op.setValue("value");
		System.out.println(op);
		Assertions.assertEquals("Key[key];Value[value]", op.toString());

	}


}
