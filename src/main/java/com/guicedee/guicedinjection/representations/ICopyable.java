package com.guicedee.guicedinjection.representations;

import com.fasterxml.jackson.databind.*;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.logger.LogFactory;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;

public interface ICopyable<J>
{
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
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return (J) this;
	}
	
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
	 * Performs a deep copy based on any jackson specifications
	 *
	 * @param source
	 * @return
	 */
	default J updateFrom(Object source)
	{
		ObjectMapper om = GuiceContext.get(Key.get(ObjectMapper.class, Names.named("Default")));
		try
		{
			String jsonFromSource = om.writeValueAsString(source);
			ObjectReader objectReader = om.readerForUpdating(this);
			objectReader.readValue(jsonFromSource);
		}
		catch (IOException e)
		{
			LogFactory.getLog("ICopyable")
			          .log(Level.SEVERE, "Cannot write or read source/destination", e);
		}
		return (J) this;
	}
}
