package com.guicedee.guicedinjection.interfaces;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer
		extends JsonSerializer<LocalDateTime>
{
	public static String LocalDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS";

	public LocalDateTimeSerializer()
	{
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(value.format(DateTimeFormatter.ofPattern(LocalDateTimeFormat)));
	}
}
