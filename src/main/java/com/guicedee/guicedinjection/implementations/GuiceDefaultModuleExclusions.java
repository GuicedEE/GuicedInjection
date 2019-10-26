package com.guicedee.guicedinjection.implementations;

import com.guicedee.guicedinjection.interfaces.IGuiceScanJarExclusions;
import com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultModuleExclusions
		implements IGuiceScanModuleExclusions<GuiceDefaultModuleExclusions>,
				   IGuiceScanJarExclusions<GuiceDefaultModuleExclusions> {
	@Override
	public @NotNull Set<String> excludeModules() {
		Set<String> strings = new HashSet<>();

		strings.add("com.guicedee.jpms.guicedinjection");
		strings.add("com.guicedee.jpms.logmaster");

		strings.add("com.google.guice");

		strings.add("io.github.classgraph");
		strings.add("java.validation");

		strings.add("com.fasterxml.jackson.core");
		strings.add("com.fasterxml.jackson.databind");
		strings.add("com.fasterxml.jackson.datatype.jdk8");
		strings.add("com.fasterxml.jackson.datatype.jsr310");

		strings.add("java.logging");
		strings.add("aopalliance");
		strings.add("javax.inject");
		strings.add("com.fasterxml.jackson.annotation");
		strings.add("com.google.common");

		return strings;
	}

	@NotNull
	@Override
	public Set<String> excludeJars() {
		Set<String> jarExclusions = new HashSet<>();
		jarExclusions.add("guiced-injection-*");
		jarExclusions.add("jwebmp-log-master-*");

		jarExclusions.add("animal-sniffer-annotations-*");
		jarExclusions.add("antlr-*");
		jarExclusions.add("aopalliance-*");
		jarExclusions.add("apiguardian-api-*");
		jarExclusions.add("assertj-*");
		jarExclusions.add("classmate-*");
		jarExclusions.add("guice-*");
		jarExclusions.add("checker-qual-*");
		jarExclusions.add("classgraph-*");

		jarExclusions.add("error-prone-annotations-*");
		jarExclusions.add("guava-*");
		jarExclusions.add("j2objc-*");
		jarExclusions.add("j2objc-annotations-*");

		jarExclusions.add("jackson-annotations-*");
		jarExclusions.add("jackson-core-*");
		jarExclusions.add("jackson-databind-*");
		jarExclusions.add("jackson-datatype-jdk8-*");
		jarExclusions.add("jackson-datatype-jsr310-*");
		jarExclusions.add("jackson-module-parameter-names-*");

		jarExclusions.add("javax.inject-*");
		jarExclusions.add("validation-api-*");

		//Testing
		jarExclusions.add("hamcrest-*");
		jarExclusions.add("sl4j-*");

		jarExclusions.add("junit-*");
		jarExclusions.add("mockito-*");
		jarExclusions.add("objenesis-*");
		jarExclusions.add("opentest4j-*");

		return jarExclusions;
	}


}
