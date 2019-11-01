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

		strings.add("za.co.bayport.jpms.guicedinjection");
		strings.add("za.co.bayport.jpms.logmaster");

		strings.add("com.google.guice");

		strings.add("io.github.classgraph");


		strings.add("com.fasterxml.jackson.core");
		strings.add("com.fasterxml.jackson.databind");
		strings.add("com.fasterxml.jackson.datatype.jdk8");
		strings.add("com.fasterxml.jackson.datatype.jsr310");

		strings.add("java.logging");
		strings.add("aopalliance");
		strings.add("javax.inject");
		strings.add("com.fasterxml.jackson.annotation");
		strings.add("com.google.common");
		strings.add("com.fasterxml.jackson.module.guice");
		strings.add("dom4j");
		strings.add("io.undertow.parser.generator");
		strings.add("jakarta.activation");
		strings.add("jandex");
		strings.add("net.bytebuddy");
		strings.add("org.apache.commons.codec");
		strings.add("org.apache.commons.collections4");
		strings.add("org.apache.commons.compress");
		strings.add("org.apache.commons.io");
		strings.add("org.apache.commons.lang3");
		strings.add("org.apache.commons.logging");
		strings.add("org.apache.commons.math3");
		strings.add("org.apache.cxf");
		strings.add("org.apache.poi");
		strings.add("org.apache.poi.ooxml");
		strings.add("org.apache.xmlbeans");
		strings.add("org.hibernate.commons.annotations");
		strings.add("org.hibernate.orm.core");
		strings.add("org.hibernate.validator");
		strings.add("org.jboss.logging");
		strings.add("org.json");
		strings.add("tm.bitronix.btm");
		strings.add("undertow.core");
		strings.add("undertow.servlet");
		strings.add("undertow.websockets.jsr");
		strings.add("xnio");
		strings.add("java.annotation");
		strings.add("java.compiler");
		strings.add("java.json");
		strings.add("java.validation");
		strings.add("java.ws.rs");
		strings.add("java.xml");
		strings.add("java.xml.bind");
		strings.add("java.xml.crypto");
		strings.add("java.xml.soap");
		strings.add("java.xml.ws");
		strings.add("javassist");
		strings.add("javax.el");
		strings.add("javax.servlet.api");
		strings.add("javax.websocket.api");

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
