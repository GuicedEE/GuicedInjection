package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import com.guicedee.logger.LogFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.logging.Level;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class LocalDateDeserializer
		extends JsonDeserializer<LocalDate>
{
	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	public LocalDate convert(String value)
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
		if (value.length() == 7)
		{
			value = new StringBuilder(value).insert(value.length() - 1, 0)
			                              .toString();
		}
        LocalDate time = new LocalDateTimeDeserializer().convert(value).toLocalDate();
		if (time == null)
		{
			LogFactory.getLog(LocalDateTimeDeserializer.class).log(Level.WARNING,"Unable to determine local date from string - [" + value + "]");
		}
		return time;
	}
}
