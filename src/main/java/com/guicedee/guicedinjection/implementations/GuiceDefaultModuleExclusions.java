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

	@NotNull
	@Override
	public Set<String> excludeJars() {
		Set<String> strings = new HashSet<>();
		strings.add("guiced-persistence-hibernateproperties-reader-*");
		strings.add("guiced-persistence-systemproperties-reader-*");
		strings.add("guiced-injection-*");
		strings.add("jwebmp-undertow-*");
		strings.add("guiced-persistence-wildfly-*");
		strings.add("uadetector-core-*");
		strings.add("uadetector-servlet-*");
		strings.add("uadetector-websockets-*");
		strings.add("jwebmp-websockets-*");
		strings.add("commons-io-*");
		strings.add("commons-lang3-*");
		strings.add("commons-text-*");
		strings.add("javax.websocket-api-*");
		strings.add("xnio-api-*");
		strings.add("xnio-nio-*");
		strings.add("jwebmp-log-master-*");
		strings.add("c3p0-*");
		strings.add("guiced-servlets-request-scoper-*");
		strings.add("mchange-commons-*");
		strings.add("hibernate-c3p0-*");
		strings.add("btm-*");
		strings.add("guiced-persistence-jpa-*");
		strings.add("guice-servlet-*");
		strings.add("guiced-servlets-*");

		strings.add("javax.servlet-api-*");
		strings.add("animal-sniffer-annotations-*");
		strings.add("antlr-*");
		strings.add("aopalliance-*");
		strings.add("apiguardian-api-*");
		strings.add("assertj-*");
		strings.add("classmate-*");
		strings.add("guice-*");
		strings.add("checker-qual-*");
		strings.add("classgraph-*");
		strings.add("guiced-servlets-jsf-*");
		strings.add("javax.faces-*");
		strings.add("javax.el-*");
		strings.add("javax.servlet-*");
		strings.add("guiced-servlets-jsf-*");
		strings.add("javax.faces-*");
		strings.add("javax.el-*");
		strings.add("javax.servlet-*");
		strings.add("guiced-persistence-*");
		strings.add("guiced-persistence-*");

		strings.add("byte-buddy-*");

		strings.add("dom4j-*");
		strings.add("guice-persist-*");

		strings.add("hibernate-core-*");
		strings.add("hibernate-commons-annotations-*");
		strings.add("hibernate-jcache-*");
		strings.add("hibernate-jpamodelgen-*");
		strings.add("hibernate-validator-*");

		strings.add("javax.persistence-*");

		strings.add("javax.transaction-api-*");
		strings.add("javax.persistence-api-*");

		strings.add("jaxb-api-*");
		strings.add("jboss-logging-*");

		strings.add("byte-buddy-*");

		strings.add("dom4j-*");
		strings.add("guice-persist-*");

		strings.add("hibernate-core-*");
		strings.add("hibernate-commons-annotations-*");
		strings.add("hibernate-jcache-*");
		strings.add("hibernate-jpamodelgen-*");
		strings.add("hibernate-validator-*");

		strings.add("javax.persistence-*");

		strings.add("javax.transaction-api-*");
		strings.add("javax.persistence-api-*");

		strings.add("jaxb-api-*");
		strings.add("jboss-logging-*");

		strings.add("hazelcast-*");
		strings.add("cache-annotations-ri-guice-*");
		strings.add("hibernate-jcache-*");
		strings.add("jboss-logging-*");
		strings.add("cache-annotations-ri-common-*");
		strings.add("cache-api-*");

		strings.add("error-prone-annotations-*");
		strings.add("guava-*");
		strings.add("j2objc-*");
		strings.add("j2objc-annotations-*");

		strings.add("jackson-annotations-*");
		strings.add("jackson-core-*");
		strings.add("jackson-databind-*");
		strings.add("jackson-datatype-jdk8-*");
		strings.add("jackson-datatype-jsr310-*");
		strings.add("jackson-module-parameter-names-*");

		strings.add("javax.inject-*");
		strings.add("validation-api-*");

		//Testing
		strings.add("hamcrest-*");
		strings.add("sl4j-*");

		strings.add("junit-*");
		strings.add("mockito-*");
		strings.add("objenesis-*");
		strings.add("opentest4j-*");

		return strings;
	}


}
