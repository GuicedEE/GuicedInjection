package com.guicedee.tests;

import com.guicedee.client.services.config.IFileContentsScanner;
import io.github.classgraph.ResourceList;

import java.util.HashMap;
import java.util.Map;

public class CustomClassScannerI
		implements IFileContentsScanner {
	@Override
	public Map<String, ResourceList.ByteArrayConsumer> onMatch() {
		Map<String, ResourceList.ByteArrayConsumer> map = new HashMap<>();
		map.put("customfile.sql", (resource, byteArray) -> System.out.println("Found custom sql in test... - " + resource.getPathRelativeToClasspathElement()));
		return map;
	}
}
