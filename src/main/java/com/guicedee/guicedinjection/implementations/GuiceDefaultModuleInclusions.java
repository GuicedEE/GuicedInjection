package com.guicedee.guicedinjection.implementations;

import com.guicedee.guicedinjection.interfaces.IGuiceScanModuleInclusions;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GuiceDefaultModuleInclusions
		implements IGuiceScanModuleInclusions<GuiceDefaultModuleInclusions>
{
	@Override
	public @NotNull
	Set<String> includeModules()
	{
		Set<String> strings = new HashSet<>();
		return strings;
	}
}
