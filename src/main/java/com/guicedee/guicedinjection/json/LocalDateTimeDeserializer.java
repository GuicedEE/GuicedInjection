package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class LocalDateTimeDeserializer
		extends JsonDeserializer<LocalDateTime>
{
	public static String LocalDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS";
	public static String LocalDateTimeFormat10 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	public static String LocalDateTimeFormat2 = "yyyy-MM-dd HH:mm:ss.SSSSSSSS";
	public static String LocalDateTimeFormat3 = "yyyy-MM-dd HH:mm:ss";
	public static String LocalDateTimeFormat7 = "yyyy-MM-dd HH:mm";
	public static String LocalDateTimeFormat8 = "yyyy-MM-dd HHmm";
	public static String LocalDateTimeFormat9 = "yyyy-MM-dd'T'HHmm";
	public static String LocalDateTimeFormat4 = "yyyy-MM-dd HH:mm:ss.SSS";
	public static String LocalDateTimeFormat5 = "yyyyMMddHHmmss";
	public static String LocalDateTimeFormat6 = "yyyy-MM-dd HH:mm:ss.SSSSSSS";

	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {DateTimeFormatter.ofPattern(LocalDateTimeFormat),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat10),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat6),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat2),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat3),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat4),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat5),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat7),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat8),
			                                                    DateTimeFormatter.ofPattern(LocalDateTimeFormat9)
			                                                   };

	@Override
	public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	public LocalDateTime convert(String name) throws IOException
	{
		if (Strings.isNullOrEmpty(name) || STRING_NULL.equals(name) || STRING_0.equals(name))
		{
			return null;
		}
		if (name.contains(E))
		{
			name = name.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY)
			           .substring(0, name.indexOf(E) - 1);
		}
		LocalDateTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = LocalDateTime.parse(name, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			throw new IOException("Unable to determine local date time from string - [" + name + "]");

		}
		return time;
	}
}
