package com.guicedee.guicedinjection.representations;

import com.fasterxml.jackson.databind.*;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.guicedee.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * Provides convenience methods to copy or update fields from another object.
 *
 * @param <J> the concrete type of the implementing class
 */
public interface ICopyable<J>
{
	/**
	 * Logger used for copy operations.
	 */
	Logger log = LogManager.getLogger("ICopyable");

	/**
	 * Updates non-null fields from the provided source instance into {@code this}.
	 *
	 * @param source the source instance to read from
	 * @return {@code this} instance, for chaining
	 */
	default J updateNonNullField(J source)
	{
		Map<String, Field> trg = asMap(getClass().getDeclaredFields());
		for (Map.Entry<String, Field> entry : trg.entrySet())
		{
			String key = entry.getKey();
			Field f = entry.getValue();
			f.setAccessible(true);
			Field fieldTarget = trg.get(f.getName());
			if (null != fieldTarget)
			{
				try
				{
					Object value = f.get(source);
					if (value != null)
					{
						fieldTarget.set(this, value);
					}
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return (J) this;
	}

	/**
	 * Converts an array of fields into a map keyed by field name, skipping static fields.
	 *
	 * @param fields the fields to map
	 * @return a map of field names to fields
	 */
	default Map<String, Field> asMap(Field[] fields)
	{
		Map<String, Field> m = new HashMap<>();
		for (Field f : fields)
		{
			if ((Modifier.isStatic(f.getModifiers())))
			{
				continue;
			}
			f.setAccessible(true);
			m.put(f.getName(), f);
		}
		return m;
	}

	/**
	 * Performs a deep update based on Jackson serialization rules.
	 *
	 * @param source the source object to copy from
	 * @return {@code this} instance, for chaining
	 */
	default J updateFrom(Object source)
	{
		ObjectMapper om = IGuiceContext.get(Key.get(ObjectMapper.class, Names.named("Default")));
		try
		{
			String jsonFromSource = om.writeValueAsString(source);
			ObjectReader objectReader = om.readerForUpdating(this);
			objectReader.readValue(jsonFromSource);
		} catch (IOException e)
		{
			log.error("Cannot write or read source/destination", e);
		}
		return (J) this;
	}
}
