package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer
		extends JsonSerializer<LocalDateTime>
{
	public ZonedDateTimeSerializer()
	{
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
	}
}
