package com.jwebmp.guiceinjection;

import com.jwebmp.guiceinjection.interfaces.GuiceConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuiceConfigTest
		implements GuiceConfigurator
{
	@Test
	public void testConfig()
	{
		GuiceConfig config = GuiceContext.get(GuiceConfig.class);
		assertTrue(config.isFieldAnnotationScanning());
		assertTrue(config.isFieldInfo());
		assertTrue(config.isIgnoreFieldVisibility());
		assertTrue(config.isIgnoreMethodVisibility());
		assertTrue(config.isMethodInfo());
		assertTrue(config.isWhiteList());
		assertTrue(config.isFieldScanning());
	}

	@Override
	public GuiceConfig configure(GuiceConfig config)
	{
		config.setIgnoreMethodVisibility(true)
		      .setMethodAnnotationIndexing(true)
		      .setWhiteList(true)
		      .setFieldAnnotationScanning(true)
		      .setFieldInfo(true)
		      .setFieldScanning(true)
		      .setIgnoreFieldVisibility(true)
		      .setMethodInfo(true);
		return config;
	}
}
