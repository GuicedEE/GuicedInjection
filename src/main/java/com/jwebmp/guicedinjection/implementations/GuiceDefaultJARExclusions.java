package com.jwebmp.guicedinjection.implementations;

import com.jwebmp.guicedinjection.interfaces.IGuiceScanJarExclusions;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultJARExclusions
		implements IGuiceScanJarExclusions<GuiceDefaultJARExclusions>
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
		jarExclusions.add("btm*");
		jarExclusions.add("hamcrest*");
		jarExclusions.add("cache-annotations-ri-common*");
		jarExclusions.add("cache-annotations-ri-guice*");
		jarExclusions.add("cache-api*");
		jarExclusions.add("classmate*");
		jarExclusions.add("commons-io*");
		jarExclusions.add("commons-lang*");
		jarExclusions.add("commons-lang3*");
		jarExclusions.add("commons-math*");
		jarExclusions.add("commons-math3*");
		jarExclusions.add("commons-text*");
		jarExclusions.add("dom4j*");
		jarExclusions.add("hazelcast-all*");

		jarExclusions.add("hibernate-core*");
		jarExclusions.add("hibernate-commons-annotations*");
		jarExclusions.add("hibernate-jcache*");
		jarExclusions.add("hibernate-jpamodelgen*");
		jarExclusions.add("hibernate-validator*");


		jarExclusions.add("jandex*");
		jarExclusions.add("javassist*");
		jarExclusions.add("javax.activation-api*");
		jarExclusions.add("javax-inject*");
		jarExclusions.add("javax.mail*");
		jarExclusions.add("javax.persistence*");
		jarExclusions.add("javax.servlet-api*");
		jarExclusions.add("javax.transaction-api*");
		jarExclusions.add("javax.websocket-api*");
		jarExclusions.add("jaxb-api*");
		jarExclusions.add("jboss-logging*");
		jarExclusions.add("json-*");
		jarExclusions.add("jsr250*");
		jarExclusions.add("minimal-json*");
		jarExclusions.add("mssql-jdbc*");
		jarExclusions.add("quality-check*");
		jarExclusions.add("sl4j*");
		jarExclusions.add("uadetector*");
		jarExclusions.add("undertow*");
		jarExclusions.add("undertow-websockets-jsr*");
		jarExclusions.add("validation-api*");
		jarExclusions.add("xnio-api*");
		jarExclusions.add("xnio-nio*");



		jarExclusions.add("junit*");
		jarExclusions.add("mockito*");
		jarExclusions.add("objenesis*");
		jarExclusions.add("opentest4j*");
		jarExclusions.add("checker-qual*");
		jarExclusions.add("classgraph*");
		jarExclusions.add("error-prone-annotations*");
		jarExclusions.add("guava*");

		jarExclusions.add("guice*");
		jarExclusions.add("guice-persist*");
		jarExclusions.add("guice-servlet*");

		jarExclusions.add("j2objc*");

		jarExclusions.add("jackson-annotations*");
		jarExclusions.add("jackson-core*");
		jarExclusions.add("jackson-databind*");
		jarExclusions.add("jackson-datatype-jdk8*");
		jarExclusions.add("jackson-datatype-jsr310*");
		jarExclusions.add("jackson-module-parameter-names*");

		jarExclusions.add("javax.inject*");

		jarExclusions.add("jwebmp-log-master*");
		jarExclusions.add("guiced-injection*");
		return jarExclusions;
	}
}
