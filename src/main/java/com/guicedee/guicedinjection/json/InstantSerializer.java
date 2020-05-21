package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InstantSerializer
		extends JsonSerializer<Instant>
{
	public InstantSerializer()
	{
	}

	@Override
	public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
	}
}
