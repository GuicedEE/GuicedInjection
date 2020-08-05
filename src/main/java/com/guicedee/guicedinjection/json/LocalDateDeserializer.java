package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

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

	public LocalDate convert(String name) throws IOException
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
		if (name.length() == 7)
		{
			name = new StringBuilder(name).insert(name.length() - 1, 0)
			                              .toString();
		}
        LocalDate time = new LocalDateTimeDeserializer().convert(name).toLocalDate();
		if (time == null)
		{
			throw new IOException("Unable to determine local date from string - " + name);
		}
		return time;
	}
}
