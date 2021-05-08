package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToCharacterSet
		extends JsonDeserializer<Set<Character>>
{
	@Override
	public Set<Character> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		Set<Character> chars = new LinkedHashSet<>();
		String value = p.getValueAsString();
		if (Strings.isNullOrEmpty(value))
		{
			return chars;
		}
		value = StringEscapeUtils.unescapeJava(value);
		value.chars().forEach(a->chars.add((char)a));
		
		return chars;
	}
}
