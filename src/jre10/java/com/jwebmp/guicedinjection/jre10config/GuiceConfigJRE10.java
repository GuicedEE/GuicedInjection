package com.jwebmp.guicedinjection.jre10config;

import com.jwebmp.guicedinjection.GuiceConfig;
import com.jwebmp.guicedinjection.interfaces.GuiceConfigurator;
import com.jwebmp.logger.LogFactory;

import java.util.logging.Level;

public class GuiceConfigJRE10 implements GuiceConfigurator
{
	public GuiceConfig configure(GuiceConfig config)
	{
		LogFactory.getLog("GuiceConfigJRE10")
		          .log(Level.WARNING, "JRE 10 requires the configuration to be package white listed. Please ensure you white list your packages using the PackageContentScanner Service.");
		return config.setWhiteList(true);
	}
}
