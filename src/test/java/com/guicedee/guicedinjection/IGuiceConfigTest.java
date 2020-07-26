package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;
import com.guicedee.logger.LogFactory;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

public class IGuiceConfigTest
		implements IGuiceConfigurator
{
	@Test
	public void testConfig()
	{
		LogFactory.configureConsoleColourOutput(Level.FINE);
		GuiceContext.instance()
		            .loadIGuiceConfigs()
		            .add(new IGuiceConfigTest());
		GuiceContext.inject();
		GuiceConfig config = GuiceContext.get(GuiceConfig.class);

		assertTrue(config.isServiceLoadWithClassPath());
		assertTrue(config.isAnnotationScanning());
		assertTrue(config.isFieldInfo());
		assertTrue(config.isIgnoreFieldVisibility());
		assertTrue(config.isIgnoreMethodVisibility());
		assertTrue(config.isMethodInfo());
		assertTrue(config.isWhiteListPackages());
		assertTrue(config.isFieldScanning());
	}

	@Override
	public GuiceConfig configure(GuiceConfig config)
	{
		config.setIgnoreMethodVisibility(true)
		      .setExcludeModulesAndJars(true)
		      .setServiceLoadWithClassPath(true)
		      .setExcludePaths(true)
		      .setAllowPaths(true)
		      .setWhiteListPackages(true)
		      .setAnnotationScanning(true)
		      .setPathScanning(true)
		      .setClasspathScanning(true)
		      .setFieldInfo(true)
		      .setFieldScanning(true)
		      .setIgnoreFieldVisibility(true)
		      .setMethodInfo(true);
		return config;
	}


}
