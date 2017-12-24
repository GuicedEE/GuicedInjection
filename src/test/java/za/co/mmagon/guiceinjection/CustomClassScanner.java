package za.co.mmagon.guiceinjection;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;
import za.co.mmagon.guiceinjection.scanners.FileContentsScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomClassScanner implements FileContentsScanner
{
	@Override
	public Map<String, FileMatchContentsProcessorWithContext> onMatch()
	{
		Map<String, FileMatchContentsProcessorWithContext> map = new HashMap<>();
		map.put("customfile.sql", new FileMatchContentsProcessorWithContext()
		{
			@Override
			public void processMatch(File classpathElt, String relativePath, byte[] fileContents)
			{
				System.out.println("Found custom sql in test...");
			}
		});
		return map;
	}
}
