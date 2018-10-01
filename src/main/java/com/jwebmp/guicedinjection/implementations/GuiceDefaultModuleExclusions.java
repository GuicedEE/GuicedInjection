package com.jwebmp.guicedinjection.implementations;

import com.jwebmp.guicedinjection.interfaces.IGuiceScanModuleExclusions;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultModuleExclusions
		implements IGuiceScanModuleExclusions<GuiceDefaultModuleExclusions>
{
	@Override
	public @NotNull Set<String> excludeModules()
	{
		Set<String> hashy = new HashSet<>();
		hashy.add("io.github.classgraph");
		hashy.add("com.google.common");
		hashy.add("javax.inject");
		hashy.add("com.google.guice");
		hashy.add("com.fasterxml.jackson.annotation");
		hashy.add("javax.validation");
		hashy.add("com.fasterxml.jackson.datatype.jsr310");
		hashy.add("com.fasterxml.jackson.datatype.jdk9");
		hashy.add("com.fasterxml.jackson.databind");
		hashy.add("com.fasterxml.jackson.core");
		hashy.add("aopalliance");
		hashy.add("java.logging");

		//Persistence
		hashy.add("com.google.guice.extensions.persist");
		hashy.add("java.naming");
		hashy.add("java.persistence");

		hashy.add("org.json");
		hashy.add("java.sql");

		hashy.add("btm");

		hashy.add("java.transaction");
		hashy.add("javax.servlet.api");
		hashy.add("com.google.guice.extensions.servlet");

		hashy.add("uadetector.core");
		hashy.add("uadetector.resources");
		hashy.add("org.apache.commons.io");
		hashy.add("org.apache.commons.lang3");
		hashy.add("org.apache.commons.text");


		return hashy;
	}
}
