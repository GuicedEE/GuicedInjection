package com.jwebmp.guicedinjection.scanners;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;

import java.util.Map;

/**
 * Marks the class as a file scanner
 */
@FunctionalInterface
public interface FileContentsScanner
{
	/**
	 * Returns a contents processer to run on match
	 *
	 * @return the maps of file identifiers and contents
	 */
	Map<String, FileMatchContentsProcessorWithContext> onMatch();
}
