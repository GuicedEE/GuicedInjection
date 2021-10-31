package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.common.base.*;

import java.io.*;
import java.time.*;
import java.time.format.*;

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
		LocalTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = LocalTime.parse(value, format);
				if (time != null)
				{
					break;
				}
			}
			catch (DateTimeParseException p)
			{
			
			}
		}
		
		return time;
	}
}
