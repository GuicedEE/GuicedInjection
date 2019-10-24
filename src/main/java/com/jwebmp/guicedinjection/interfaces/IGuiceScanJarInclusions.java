package com.guicedee.guicedinjection.interfaces;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Marks JAR files referenced from libraries to be excluded from all scans
 */
@FunctionalInterface
public interface IGuiceScanJarInclusions<J extends IGuiceScanJarInclusions<J>>
		extends IDefaultService<J> {
	/**
	 * Excludes the given jars for scanning
	 *
	 * @return
	 */
	@NotNull Set<String> includeJars();


}
