package za.co.mmagon.guiceinjection.scanners;

import java.util.HashSet;
import java.util.Set;

public class GuiceInjectionPackageScanner implements PackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		return new HashSet<>();
	}
}
