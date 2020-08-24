package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;

/**
 * Converts most of the string knowns to boolean
 */
public class DurationToString
		extends JsonSerializer<Duration>
{
	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException
	{
		if(value == null)
			return ;
		gen.writeString(convert(value));
	}

	public String convert(@NotNull Duration value)
	{
		return value.toString();
	}
}
