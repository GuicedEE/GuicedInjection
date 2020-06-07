package com.guicedee.guicedinjection.interfaces;

import io.github.classgraph.ResourceList;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Marks the class as a file scanner
 */
@FunctionalInterface
public interface IFileContentsPatternScanner
{
	/**
	 * Returns a contents processor to run on match
	 *
	 * @return the maps of file identifiers and contents
	 */
	Map<Pattern, ResourceList.ByteArrayConsumer> onMatch();
}
