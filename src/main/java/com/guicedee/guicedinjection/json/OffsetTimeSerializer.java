package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class OffsetTimeSerializer
		extends JsonSerializer<OffsetTime>
{
	public OffsetTimeSerializer()
	{
	}

	@Override
	public void serialize(OffsetTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(DateTimeFormatter.ISO_OFFSET_TIME.format(value));
	}
}
