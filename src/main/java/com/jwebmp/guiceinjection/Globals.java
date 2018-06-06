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
package com.jwebmp.guiceinjection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * A pretty class for containing EAR or Container level global properties
 *
 * @author Marc Magon
 * @since 08 Jul 2017
 */
@javax.inject.Singleton
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public class Globals
		implements Serializable
{

	private static final long serialVersionUID = 1L;
	private final Map<String, Map<Serializable, Serializable>> globalProperties;

	/**
	 * Constructs a new Globals
	 */
	public Globals()
	{
		globalProperties = new HashMap<>();
	}

	/**
	 * Adds a key to the global application library
	 *
	 * @param key
	 * @param properties
	 */
	public void addKey(String key, Map<Serializable, Serializable> properties)
	{
		globalProperties.put(key, properties);
	}

	/**
	 * Adds a normal string string property to the library
	 *
	 * @param key
	 * @param property
	 * @param value
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
	 * @param property
	 * @param value
	 */
	public void addProperty(String key, String property, Serializable value)
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
	 * @param <V>
	 * @param key
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K extends Serializable, V extends Serializable> Map<K, V> getKey(String key)
	{
		return (Map<K, V>) globalProperties.get(key);
	}

	/**
	 * Gets a default string key and property mapping
	 *
	 * @param <V>
	 * @param key
	 * @param property
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <V extends Serializable> V getProperty(String key, String property)
	{
		return (V) globalProperties.get(key)
		                           .get(property);
	}

	/**
	 * Removes a property from any list
	 *
	 * @param key
	 * @param property
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
	 * @param property
	 */
	public void emptyProperty(String key, String property)
	{
		if (globalProperties.containsKey(key))
		{
			globalProperties.get(key)
			                .put(property, "");
		}
	}
}
