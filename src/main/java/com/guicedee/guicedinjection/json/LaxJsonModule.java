package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.guicedee.guicedinjection.json.mapkeys.*;

import java.io.IOException;
import java.time.*;

public class LaxJsonModule extends SimpleModule
{
	public LaxJsonModule()
	{
		super("GuicedTimeHandler", Version.unknownVersion());
		
		addDeserializer(Boolean.class, new StringToBoolean())
				.addDeserializer(boolean.class, new JsonDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
					{
						return new StringToBool().deserialize(p, ctxt);
					}
				})
				.addDeserializer(int.class, new JsonDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
					{
						return new StringToIntRelaxed().deserialize(p, ctxt);
					}
				})
				.addDeserializer(Integer.class, new JsonDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
					{
						return new StringToIntegerRelaxed().deserialize(p, ctxt);
					}
				})
				.addDeserializer(Duration.class, new StringToDurationTimeSeconds())
				.addDeserializer(LocalDate.class, new LocalDateDeserializer())
				.addDeserializer(LocalTime.class, new LocalTimeDeserializer())
				.addSerializer(LocalTime.class, new LocalTimeSerializer())
				.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
				.addDeserializer(Integer.class, new StringToIntegerRelaxed())
				.addSerializer(LocalDate.class, new LocalDateSerializer())
				.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
				.addSerializer(Duration.class, new DurationToString())
				.addDeserializer(Instant.class, new InstantDeserializer())
				.addSerializer(Instant.class, new InstantSerializer())
				.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer())
				.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer())
				.addDeserializer(OffsetTime.class, new OffsetTimeDeserializer())
				.addSerializer(OffsetTime.class, new OffsetTimeSerializer())
				.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer())
				
				.addKeyDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializerKey())
				.addKeyDeserializer(LocalDateTime.class, new LocalDateTimeDeserializerKey())
				.addKeyDeserializer(LocalDate.class, new LocalDateDeserializerKey())
		
		;
	}
	
}
