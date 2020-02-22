package com.guicedee.guicedinjection.interfaces;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer
		extends JsonDeserializer<LocalDateTime>
{
	public static String LocalDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS";
	public static String LocalDateTimeFormat2 = "yyyy-MM-dd HH:mm:ss.SSSSSSSS";
	public static String LocalDateTimeFormat3 = "yyyy-MM-dd HH:mm:ss";

	@Override
	public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocalDateTimeFormat);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(LocalDateTimeFormat2);
		DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern(LocalDateTimeFormat3);
		LocalDateTime time = null;
		try
		{
			time = LocalDateTime.parse(name, formatter);
		}
		catch (DateTimeParseException dtpe)
		{
			try
			{
				time = LocalDateTime.parse(name, formatter2);
			}
			catch (DateTimeParseException dtpe2)
			{
				time = LocalDateTime.parse(name, formatter3);
			}
		}
		return time;
	}
}
