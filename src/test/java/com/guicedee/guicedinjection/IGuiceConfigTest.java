package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IGuiceConfigTest
		implements IGuiceConfigurator {
	@Test
	public void testConfig() {
		GuiceConfig config = GuiceContext.get(GuiceConfig.class);
		assertTrue(config.isAnnotationScanning());
		assertTrue(config.isFieldInfo());
		assertTrue(config.isIgnoreFieldVisibility());
		assertTrue(config.isIgnoreMethodVisibility());
		assertTrue(config.isMethodInfo());
		assertTrue(config.isWhiteListPackages());
		assertTrue(config.isFieldScanning());
	}

	@Override
	public GuiceConfig configure(GuiceConfig config) {
		config.setIgnoreMethodVisibility(true)
			  .setWhiteListPackages(true)
			  .setAnnotationScanning(true)
			  .setFieldInfo(true)
			  .setFieldScanning(true)
			  .setIgnoreFieldVisibility(true)
			  .setMethodInfo(true);
		return config;
	}


}
