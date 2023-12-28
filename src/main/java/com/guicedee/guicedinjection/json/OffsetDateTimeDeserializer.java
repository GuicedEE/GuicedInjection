package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

import static com.guicedee.guicedinjection.json.LocalDateTimeDeserializer.formats;
import static com.guicedee.guicedinjection.json.StaticStrings.*;

@Log
public class OffsetDateTimeDeserializer
		extends JsonDeserializer<OffsetDateTime>
{
	@Override
	public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	public OffsetDateTime convert(String value)
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
		OffsetDateTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = OffsetDateTime.parse(value, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			
			LocalDateTime convert = new LocalDateTimeDeserializer().convert(value);
			if (convert != null)
			{
				time = convertToUTCDateTime(convert);
			}else
			{
				log.log(Level.WARNING, "Unable to determine offset datetime from string - [" + value + "]");
			}
		}
		return time;
	}
	private OffsetDateTime convertToUTCDateTime(LocalDateTime ldt)
	{
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.systemDefault());
		ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameLocal(ZoneId.of("UTC"));
		OffsetDateTime offsetDateTime = utcZonedDateTime.toOffsetDateTime();
		return offsetDateTime;
	}
	
}
