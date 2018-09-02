package com.jwebmp.guicedinjection.implementations;

import com.jwebmp.guicedinjection.interfaces.IGuiceScanJarExclusions;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultJARExclusions
		implements IGuiceScanJarExclusions
{
	@NotNull
	@Override
	public Set<String> excludeJars()
	{
		Set<String> jarExclusions = new HashSet<>();
		jarExclusions.add("animal-sniffer-annotations*");
		jarExclusions.add("antlr*");
		jarExclusions.add("aopalliance*");
		return jarExclusions;
	}
}
