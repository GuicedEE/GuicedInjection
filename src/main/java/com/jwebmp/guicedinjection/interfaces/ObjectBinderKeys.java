package com.jwebmp.guicedinjection.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class ObjectBinderKeys
{
	/**
	 * The default object mapping
	 */
	public static final Key<ObjectMapper> DefaultObjectMapper = Key.get(ObjectMapper.class, Names.named("Default"));
	/**
	 * The default object writer
	 */
	public static final Key<ObjectWriter> JSONObjectWriter = Key.get(ObjectWriter.class, Names.named("JSON"));
	/**
	 * The default object writer for tiny
	 */
	public static final Key<ObjectWriter> JSONObjectWriterTiny = Key.get(ObjectWriter.class, Names.named("JSONTiny"));
	/**
	 * The object reader for tiny
	 */
	public static final Key<ObjectReader> JSONObjectReader = Key.get(ObjectReader.class, Names.named("JSON"));


}
