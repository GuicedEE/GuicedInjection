package com.jwebmp.guicedinjection;

import com.jwebmp.guicedinjection.interfaces.IFileContentsScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomClassScannerI
		implements IFileContentsScanner
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
