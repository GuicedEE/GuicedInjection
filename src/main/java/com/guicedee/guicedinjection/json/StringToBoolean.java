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
	public static boolean nullable = true;

	@Override
	public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		return convert(p.getValueAsString());
	}

	public Boolean convert(String value)
	{
		if (Strings.isNullOrEmpty(value))
		{
			return null;
		}
		value = value.trim().toLowerCase();
		switch (value)
		{
			case "1":
			case "1.0":
			case "y":
			case "yes":
			case "true":
				return true;
			case "0":
			case "0.0":
			case "no":
			case "n":
			case "false":
				return false;
		}
		if(nullable)
			return null;
		else return false;
	}
}
