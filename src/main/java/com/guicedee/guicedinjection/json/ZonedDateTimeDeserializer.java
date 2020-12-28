package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import com.guicedee.logger.LogFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.logging.Level;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class ZonedDateTimeDeserializer
		extends JsonDeserializer<ZonedDateTime>
{
	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {
					                                                   new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_ZONED_DATE_TIME)
					                                                                                 .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
					                                                                                 .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
					                                                                                 .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
					                                                                                 .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
					                                                                                 .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
					                                                                                 .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
							                                                   .toFormatter()
			                                                   };

	@Override
	public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	public ZonedDateTime convert(String value)
	{
		if (Strings.isNullOrEmpty(value) || STRING_NULL.equalsIgnoreCase(value) || STRING_0.equals(value))
		{
			return null;
		}
		if (value.contains(E))
		{
			value = value.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY)
			             .substring(0, value.indexOf(E) - 1);
		}
		ZonedDateTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = ZonedDateTime.parse(value, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			LogFactory.getLog(ZonedDateTimeDeserializer.class).log(Level.WARNING,"Unable to determine local date from string - [" + value + "]");

		}
		return time;
	}
}
