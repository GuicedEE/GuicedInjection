/*
 * Copyright (C) 2017 Marc Magon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jwebmp.guicedinjection.properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.logger.LogFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * A pretty class for containing EAR or Container level global properties.
 * <p>
 * Key -> Map ID -> Property
 *
 * @author Marc Magon
 * @since 08 Jul 2017
 */
@javax.inject.Singleton
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public class GlobalProperties
		implements Serializable
{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogFactory.getLog("GlobalPropertyMaps");
	private final Map<String, Map<Object, Object>> globalProperties;

	/**
	 * Constructs a new GlobalProperties
	 */
	public GlobalProperties()
	{
		globalProperties = new HashMap<>();
	}

	/**
	 * Adds a key to the global application library
	 *
	 * @param key
	 * 		Adds a key into the global library
	 * @param properties
	 * 		Puts the property map into the global settings
	 */
	public void addKey(String key, Map<Object, Object> properties)
	{
		globalProperties.put(key, properties);
	}

	/**
	 * Adds a normal string string property to the library
	 *
	 * @param key
	 * 		Takes the key for the property
	 * @param property
	 * 		The property to apply
	 * @param value
	 * 		The value to apply
	 */
	public void addProperty(String key, String property, String value)
	{
		if (!globalProperties.containsKey(key))
		{
			globalProperties.put(key, new HashMap<>());
		}
		globalProperties.get(key)
		                .put(property, value);
	}

	/**
	 * Adds a normal string string property to the library
	 *
	 * @param key
	 * 		The key and property to add
	 * @param property
	 * 		The property to add
	 * @param value
	 * 		The value to add
	 */
	public void addProperty(String key, String property, Object value)
	{
		if (!globalProperties.containsKey(key))
		{
			globalProperties.put(key, new HashMap<>());
		}
		globalProperties.get(key)
		                .put(property, value);
	}

	/**
	 * Gets the key with the given map return type
	 *
	 * @param <K>
	 * 		The key type
	 * @param <V>
	 * 		The value map type
	 * @param key
	 * 		The key
	 *
	 * @return A map to return
	 */
	@SuppressWarnings({"unchecked", "UnusedReturnValue"})
	public <K, V> Map<K, V> getKey(String key)
	{
		return (Map<K, V>) globalProperties.get(key);
	}

	/**
	 * Gets a default string key and property mapping
	 *
	 * @param <V>
	 * 		The value type
	 * @param key
	 * 		The key
	 * @param property
	 * 		And properties map to retrieve from
	 *
	 * @return The value of the mapped key and map ID
	 */
	@SuppressWarnings("unchecked")
	public <V> V getProperty(String key, String property)
	{
		return (V) globalProperties.get(key)
		                           .get(property);
	}

	/**
	 * Removes a property from any list
	 *
	 * @param key
	 * 		The key to remove
	 * @param property
	 * 		The property to remove from the assigned map
	 */
	public void removeProperty(String key, String property)
	{
		if (globalProperties.containsKey(key))
		{
			globalProperties.get(key)
			                .remove(property);
		}
	}

	/**
	 * Sets the property
	 *
	 * @param key
	 * 		The key to remove
	 * @param property
	 * 		The property to return
	 */
	public void emptyProperty(String key, String property)
	{
		if (globalProperties.containsKey(key))
		{
			globalProperties.get(key)
			                .put(property, "");
		}
	}

	/**
	 * Returns a JSON implementation of the toString()
	 *
	 * @return A JSON Representation
	 */
	@Override
	public String toString()
	{
		try
		{
			return GuiceContext.getInstance(ObjectMapper.class)
			                   .writeValueAsString(this);
		}
		catch (JsonProcessingException e)
		{
			log.log(Level.SEVERE, "Non-Mappable character in GlobalProperties Map, Can't toString()", e);
			return super.toString();
		}
	}
}
