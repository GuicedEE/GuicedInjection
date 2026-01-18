package com.guicedee.guicedinjection.implementations;

import com.guicedee.client.services.config.IGuiceScanModuleInclusions;

import java.util.HashSet;
import java.util.Set;

/**
 * Default module inclusion list used to constrain classpath scanning.
 */
public class GuiceDefaultModuleInclusions
		implements IGuiceScanModuleInclusions<GuiceDefaultModuleInclusions>
{
	/**
	 * Returns the default module names to include in scanning.
	 *
	 * @return a set of module names to include
	 */
	@Override
	public Set<String> includeModules()
	{
		Set<String> strings = new HashSet<>();
		return strings;
	}
}
