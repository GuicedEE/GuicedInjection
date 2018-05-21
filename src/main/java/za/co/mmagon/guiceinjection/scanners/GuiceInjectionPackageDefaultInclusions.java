package za.co.mmagon.guiceinjection.scanners;

import java.util.HashSet;
import java.util.Set;

public class GuiceInjectionPackageDefaultInclusions
		implements PackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		Set<String> strings = new HashSet<>();
		strings.add("za.co.mmagon");
		return strings;
	}
}
