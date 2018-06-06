package com.jwebmp.guiceinjection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author bpmm097
 */
public class GlobalsTest
{

	public GlobalsTest()
	{
	}

	@Test
	public void testSomeMethod()
	{
		Globals g = new Globals();
		g.addProperty("key", "property", "value");
		g.getProperty("key", "property");
		Assertions.assertEquals(g.getProperty("key", "property"), "value");
		g.getKey("key");
		g.removeProperty("key", "property");

	}

}
