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
	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
			                                                                                  .appendOptional(
					                                                                                  new DateTimeFormatterBuilder()
							                                                                                  .appendPattern("yyyy")

							                                                                                  .optionalStart()
							                                                                                  .appendLiteral("/")
							                                                                                  .optionalEnd()

							                                                                                  .optionalStart()
							                                                                                  .appendLiteral("-")
							                                                                                  .optionalEnd()

							                                                                                  .optionalStart()
							                                                                                  .appendPattern("MM")
							                                                                                  .optionalStart()
							                                                                                  .appendLiteral("/")
							                                                                                  .optionalEnd()
							                                                                                  .optionalStart()
							                                                                                  .appendLiteral("-")
							                                                                                  .optionalEnd()
							                                                                                  .optionalEnd()

							                                                                                  .optionalStart()
							                                                                                  .appendPattern("dd")
							                                                                                  .optionalEnd()
							                                                                                  .optionalStart()
							                                                                                  .appendPattern("d")
							                                                                                  .optionalEnd()
							                                                                                  .toFormatter()
			                                                                                                 )
			                                                                                  .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
			                                                                                  .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
					                                                    .toFormatter()};

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
		LocalDate time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = LocalDate.parse(name, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			throw new IOException("Unable to determine local date from string - " + name);
		}
		return time;
	}
}
