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
		hashy.add("com.jwebmp.logmaster");
		return hashy;
	}
}
