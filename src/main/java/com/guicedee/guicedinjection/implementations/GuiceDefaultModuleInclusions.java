package com.guicedee.guicedinjection.implementations;

import com.guicedee.guicedinjection.interfaces.IGuiceScanModuleInclusions;

import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultModuleInclusions
		implements IGuiceScanModuleInclusions<GuiceDefaultModuleInclusions>
{
	@Override
	public Set<String> includeModules()
	{
		Set<String> strings = new HashSet<>();
		return strings;
	}
}
