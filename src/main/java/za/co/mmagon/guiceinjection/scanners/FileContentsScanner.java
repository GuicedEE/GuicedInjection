package za.co.mmagon.guiceinjection.scanners;

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
	 * @return
	 */
	Map<String, FileMatchContentsProcessorWithContext> onMatch();
}
