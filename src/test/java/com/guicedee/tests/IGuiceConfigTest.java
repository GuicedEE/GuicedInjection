package com.guicedee.tests;

import com.guicedee.client.*;
import com.guicedee.guicedinjection.GuiceConfig;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.interfaces.IGuiceConfig;
import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log
public class IGuiceConfigTest
		implements IGuiceConfigurator
{
	@Test
	public void testConfig()
	{
		//LogFactory.configureConsoleColourOutput(Level.FINE);
		GuiceContext.instance()
		            .loadIGuiceConfigs()
		            .add(new IGuiceConfigTest());
		IGuiceContext.getContext().inject();
		GuiceConfig config = IGuiceContext.get(GuiceConfig.class);
		 config = GuiceContext.instance().getConfig();
		
		assertTrue(config.isServiceLoadWithClassPath());
		assertTrue(config.isAnnotationScanning());
		assertTrue(config.isFieldInfo());
		assertTrue(config.isIgnoreFieldVisibility());
		assertTrue(config.isIgnoreMethodVisibility());
		assertTrue(config.isMethodInfo());
		assertTrue(config.isIncludePackages());
		assertTrue(config.isFieldScanning());
	}

	@Override
	public IGuiceConfig configure(IGuiceConfig config)
	{
		config.setIgnoreMethodVisibility(true)
		      .setExcludeModulesAndJars(true)
		      .setExcludePaths(true)
		      .setAllowPaths(true)
		      .setIncludePackages(true)
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
