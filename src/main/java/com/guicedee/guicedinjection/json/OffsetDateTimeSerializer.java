package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer
		extends JsonSerializer<OffsetDateTime>
{
	public OffsetDateTimeSerializer()
	{
	}

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
	}
}
