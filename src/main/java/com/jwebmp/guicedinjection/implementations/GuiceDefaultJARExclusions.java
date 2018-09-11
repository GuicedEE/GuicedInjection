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
		jarExclusions.add("apiguardian-api*");
		jarExclusions.add("assertj*");
		jarExclusions.add("byte-buddy*");
		jarExclusions.add("hamcrest*");
		jarExclusions.add("junit*");
		jarExclusions.add("mockito*");
		jarExclusions.add("objenesis*");
		jarExclusions.add("opentest4j*");
		jarExclusions.add("validation-api*");
		jarExclusions.add("checker-qual*");
		jarExclusions.add("classgraph*");
		jarExclusions.add("error-prone-annotations*");
		jarExclusions.add("guava*");
		jarExclusions.add("guice*");
		jarExclusions.add("j2objc*");
		jarExclusions.add("jackson*");
		jarExclusions.add("javax.inject*");
		jarExclusions.add("jwebmp-log-master*");
		jarExclusions.add("guiced-injection*");
		return jarExclusions;
	}
}
