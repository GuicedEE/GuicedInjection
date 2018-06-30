package com.jwebmp.guiceinjection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

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
		g.emptyProperty("key", "property");

		g.addProperty("key1", "mapidentifier", "value");
		g.addProperty("key2", "mapidentifier", new Globals());

		g.addKey("key3", new TreeMap<>());

		System.out.println(g.toString());
	}

}
