package com.jwebmp.guicedinjection.interfaces;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Marks JAR files referenced from libraries to be excluded from all scans
 */
@FunctionalInterface
public interface IGuiceScanJarExclusions
		extends IDefaultService<IGuiceScanJarExclusions>
{
	/**
	 * Excludes the given jars for scanning
	 *
	 * @return
	 */
	@NotNull Set<String> excludeJars();
}
