package com.jwebmp.guicedinjection;

import com.jwebmp.guicedinjection.properties.GlobalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

/**
 * @author bpmm097
 */
public class GlobalPropertiesTest
{

	public GlobalPropertiesTest()
	{
	}

	@Test
	public void testSomeMethod()
	{
		GlobalProperties g = new GlobalProperties();
		g.addProperty("key", "property", "value");
		g.getProperty("key", "property");
		Assertions.assertEquals(g.getProperty("key", "property"), "value");
		g.getKey("key");
		g.removeProperty("key", "property");
		g.emptyProperty("key", "property");

		g.addProperty("key1", "mapidentifier", "value");
		g.addProperty("key2", "mapidentifier", new GlobalProperties());

		g.addKey("key3", new TreeMap<>());

		System.out.println(g.toString());
	}

}
