package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class LocalDateTimeDeserializer
		extends JsonDeserializer<LocalDateTime>
{
	public static String LocalDateTimeFormat3 = "yyyy-MM-dd HH:mm:ss";
	public static String LocalDateTimeFormat7 = "yyyy-MM-dd HH:mm";
	public static String LocalDateTimeFormat8 = "yyyy-MM-dd HHmm";
	public static String LocalDateTimeFormat9 = "yyyy-MM-dd'T'HHmm";
	public static String LocalDateTimeFormat5 = "yyyyMMddHHmmss";

	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {
					                                                   new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE)
					                                                                                 .optionalStart()
					                                                                                 .appendLiteral('T')
					                                                                                 .optionalEnd()
					                                                                                 .optionalStart()
					                                                                                 .appendLiteral(' ')
					                                                                                 .optionalEnd()
					                                                                                 .appendPattern("HH:mm:ss")
					                                                                                 .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
							                                                   .toFormatter(),
					                                                   DateTimeFormatter.ofPattern(LocalDateTimeFormat3),
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

	public LocalDateTime convert(String value) throws IOException
	{
		if (Strings.isNullOrEmpty(value) || STRING_NULL.equals(value) || STRING_0.equals(value))
		{
			return null;
		}
		if (value.contains(E))
		{
			value = value.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY)
			             .substring(0, value.indexOf(E) - 1);
		}
		value = value.replace(' ', 'T');
		LocalDateTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = LocalDateTime.parse(value, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			throw new IOException("Unable to determine local date time from string - [" + value + "]");

		}
		return time;
	}
}
