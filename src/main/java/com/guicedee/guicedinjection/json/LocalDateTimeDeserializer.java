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
	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {
					                                                   new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
					                                                                                 .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
					                                                                                 .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)
					                                                                                 .appendOptional(
							                                                                                 new DateTimeFormatterBuilder().appendOptional(
									                                                                                 DateTimeFormatter.ISO_LOCAL_DATE)
							                                                                                                               .optionalStart()
							                                                                                                               .appendLiteral('T')
							                                                                                                               .optionalEnd()
							                                                                                                               .optionalStart()
							                                                                                                               .appendLiteral(' ')
							                                                                                                               .optionalEnd()
							                                                                                                               .appendPattern("HH:mm:ss")
							                                                                                                               .appendFraction(
									                                                                                                               ChronoField.NANO_OF_SECOND, 0, 9,
									                                                                                                               true)
							                                                                                                               .toFormatter()
					                                                                                                )
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

									                                                                                 .optionalStart()
									                                                                                 .appendLiteral('T')
									                                                                                 .optionalEnd()
									                                                                                 .optionalStart()
									                                                                                 .appendLiteral(' ')
									                                                                                 .optionalEnd()

									                                                                                 .optionalStart()

									                                                                                 .optionalStart()
									                                                                                 .appendPattern("HH")


									                                                                                 .optionalStart()
									                                                                                 .appendLiteral(':')
									                                                                                 .optionalEnd()
									                                                                                 .optionalEnd()

									                                                                                 .optionalStart()
									                                                                                 .appendPattern("mm")
									                                                                                 .optionalEnd()

									                                                                                 .optionalStart()
									                                                                                 .appendLiteral(':')
									                                                                                 .optionalEnd()

									                                                                                 .optionalStart()
									                                                                                 .appendPattern("ss")
									                                                                                 .optionalEnd()

									                                                                                 .optionalEnd()

									                                                                                 .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
									                                                                                 .toFormatter()
					                                                                                                )
					                                                                                 .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
					                                                                                 .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
					                                                                                 .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
					                                                                                 .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
					                                                                                 .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
					                                                                                 .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
							                                                   .toFormatter()
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
