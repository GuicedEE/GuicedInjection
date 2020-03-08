package com.guicedee.guicedinjection.implementations;

import com.guicedee.guicedinjection.interfaces.IGuiceScanModuleExclusions;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultModuleExclusions
		implements IGuiceScanModuleExclusions<GuiceDefaultModuleExclusions> {
	@Override
	public @NotNull Set<String> excludeModules() {
		Set<String> strings = new HashSet<>();
		strings.add("aopalliance");
		strings.add("btm");
		strings.add("c3p0");
		strings.add("cache.annotations.ri.guice");
		strings.add("cache.api");
		strings.add("com.fasterxml.jackson.annotation");
		strings.add("com.fasterxml.jackson.core");
		strings.add("com.fasterxml.jackson.databind");
		strings.add("com.fasterxml.jackson.datatype.jdk8");
		strings.add("com.fasterxml.jackson.datatype.jsr310");
		strings.add("com.fasterxml.jackson.module.guice");
		strings.add("com.google.common");
		strings.add("com.google.guice");
		strings.add("com.google.guice.extensions.persist");
		strings.add("com.google.guice.extensions.servlet");
		strings.add("com.guicedee.guicedhazelcast");
		strings.add("com.guicedee.guicedinjection");
		strings.add("com.guicedee.guicedpersistence");
		strings.add("com.guicedee.guicedpersistence.btm");
		strings.add("com.guicedee.guicedpersistence.c3p0");
		strings.add("com.guicedee.guicedpersistence.jpa");
		strings.add("com.guicedee.guicedpersistence.readers.hibernateproperties");
		strings.add("com.guicedee.guicedpersistence.readers.systemproperties");
		strings.add("com.guicedee.guicedservlets");
		strings.add("com.guicedee.guicedservlets.jsf");
		strings.add("com.guicedee.guicedservlets.requestscoped");
		strings.add("com.guicedee.logmaster");
		strings.add("com.guicedee.undertow");
		strings.add("com.guicedee.websockets");
		strings.add("com.jwebmp.guicedpersistence.wildfly");
		strings.add("dom4j");
		strings.add("hazelcast.all");
		strings.add("io.github.classgraph");
		strings.add("io.undertow.parser.generator");
		strings.add("jakarta.activation");
		strings.add("jandex");
		strings.add("java.annotation");
		strings.add("java.compiler");
		strings.add("java.desktop");
		strings.add("java.json");
		strings.add("java.logging");
		strings.add("java.naming");
		strings.add("java.managment");
		strings.add("jdk.compiler");
		strings.add("jdk.javadoc");
		strings.add("java.persistence");
		strings.add("java.sql");
		strings.add("java.transaction");
		strings.add("java.validation");
		strings.add("java.ws.rs");
		strings.add("java.xml");
		strings.add("java.xml.bind");
		strings.add("java.xml.crypto");
		strings.add("java.xml.soap");
		strings.add("java.xml.ws");
		strings.add("javassist");
		strings.add("javax.el");
		strings.add("javax.faces");
		strings.add("javax.inject");
		strings.add("javax.servlet.api");
		strings.add("javax.websocket.api");
		strings.add("jboss.logging");
		strings.add("net.bytebuddy");
		strings.add("org.apache.commons.codec");
		strings.add("org.apache.commons.collections4");
		strings.add("org.apache.commons.compress");
		strings.add("org.apache.commons.io");
		strings.add("org.apache.commons.lang3");
		strings.add("org.apache.commons.logging");
		strings.add("org.apache.commons.math3");
		strings.add("org.apache.commons.text");
		strings.add("org.apache.cxf");
		strings.add("org.apache.poi");
		strings.add("org.apache.poi.ooxml");
		strings.add("org.apache.xmlbeans");
		strings.add("org.hibernate.commons.annotations");
		strings.add("org.hibernate.orm.core");
		strings.add("org.hibernate.orm.jcache");
		strings.add("org.hibernate.validator");
		strings.add("org.jboss.logging");
		strings.add("org.json");
		strings.add("tm.bitronix.btm");
		strings.add("undertow.core");
		strings.add("undertow.servlet");
		strings.add("undertow.websockets.jsr");
		strings.add("xnio");
		strings.add("xnio.api");
		strings.add("za.co.bayport.jpms.guicedinjection");
		strings.add("za.co.bayport.jpms.logmaster");


		return strings;
	}
}
