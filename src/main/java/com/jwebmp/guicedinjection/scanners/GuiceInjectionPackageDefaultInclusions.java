package com.jwebmp.guicedinjection.scanners;

import com.jwebmp.guicedinjection.interfaces.IPackageContentsScanner;

import java.util.HashSet;
import java.util.Set;

public class GuiceInjectionPackageDefaultInclusions
		implements IPackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		Set<String> strings = new HashSet<>();
		strings.add("com.jwebmp.guicedinjection");
		return strings;
	}
}
