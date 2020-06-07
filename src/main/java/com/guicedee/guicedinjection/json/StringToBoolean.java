package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToBoolean
		extends JsonDeserializer<Boolean>
{
	@Override
	public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		return convert(p.getValueAsString());
	}

	public Boolean convert(String value)
	{
		if (Strings.isNullOrEmpty(value))
		{
			return null;
		}
		value = value.trim();
		switch (value)
		{
			case "1":
			case "1.0":
			case "Y":
			case "Yes":
			case "true":
				return true;
			case "0":
			case "No":
			case "N":
				return false;
		}
		return null;
	}
}
