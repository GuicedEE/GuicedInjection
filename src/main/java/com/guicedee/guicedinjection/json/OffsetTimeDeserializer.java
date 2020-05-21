package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class OffsetTimeDeserializer
		extends JsonDeserializer<OffsetTime>
{
	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {
					                                                   new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_OFFSET_TIME)
					                                                                                 .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
					                                                                                 .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
					                                                                                 .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
							                                                   .toFormatter()
			                                                   };

	@Override
	public OffsetTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	public OffsetTime convert(String value) throws IOException
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
		OffsetTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = OffsetTime.parse(value, format);
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
