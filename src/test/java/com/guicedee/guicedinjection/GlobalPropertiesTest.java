package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.properties.GlobalProperties;
import com.guicedee.logger.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.logging.Level;

/**
 * @author bpmm097
 */
public class GlobalPropertiesTest {

	@Test
	public void testSomeMethod() {
		LogFactory.configureConsoleColourOutput(Level.FINE);
		GuiceContext.instance()
		            .getConfig()
		            .setServiceLoadWithClassPath(true);
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
