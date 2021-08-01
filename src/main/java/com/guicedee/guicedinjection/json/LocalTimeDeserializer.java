package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.guicedee.guicedinjection.json.LocalTimeSerializer.*;
import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class LocalTimeDeserializer
		extends JsonDeserializer<LocalTime>
{
	@Override
	public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String name = p.getValueAsString();
		return convert(name);
	}
	
	public LocalTime convert(String value)
	{
		if (Strings.isNullOrEmpty(value) || STRING_NULL.equalsIgnoreCase(value) || STRING_0.equals(value))
		{
			return null;
		}
		return LocalTime.parse(value, DateTimeFormatter.ofPattern(LocalTimeFormat));
	}
}
