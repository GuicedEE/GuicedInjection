package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class LocalTimeSerializer
		extends JsonSerializer<LocalTime>
{
	public static final DateTimeFormatter[] formats = new DateTimeFormatter[]{
			DateTimeFormatter.ofPattern("HHmmss"),
			DateTimeFormatter.ofPattern("HH:mm:ss"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSS"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"),
			DateTimeFormatter.ofPattern("HHmm"),
	};
	
	public LocalTimeSerializer()
	{
	}
	
	@Override
	public void serialize(LocalTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(convert(value));
	}
	
	public String convert(LocalTime value)
	{
		if (value == null)
		{
			return null;
		}
		return value.format(DateTimeFormatter.ofPattern("HHmmss"));
	}
}
