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
public class StringToBool
		extends JsonDeserializer
{
	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String value = p.getValueAsString();
		return convert(value);
	}

	public boolean convert(String value)
	{
		Boolean bValue = new StringToBoolean().convert(value);
		if (bValue == null)
		{
			return false;
		}
		else
		{
			return bValue;
		}
	}
}
