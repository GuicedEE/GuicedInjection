package com.guicedee.guicedinjection.json.mapkeys;

import com.fasterxml.jackson.databind.*;
import com.guicedee.guicedinjection.json.*;

import java.io.*;

public class LocalDateDeserializerKey
		extends KeyDeserializer
{
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
	{
		return new LocalDateDeserializer().convert(key);
	}
}
