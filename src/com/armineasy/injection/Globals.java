/*
 * The MIT License
 *
 * Copyright 2017 Marc Magon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.armineasy.injection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marc Magon
 * @since 08 Jul 2017
 */
@javax.inject.Singleton
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Globals implements Serializable
{

    private static final long serialVersionUID = 1L;
    private final Map<String, Map<Serializable, Serializable>> globalProperties;

    /*
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
        globalProperties.get(key).put(property, value);
    }

    /**
     * Gets the key with the given map return type
     *
     * @param <K>
     * @param <V>
     * @param key
     * @return
     */
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
     * @return
     */
    public <V extends Serializable> V getProperty(String key, String property)
    {
        return (V) globalProperties.get(key).get(property);
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
            globalProperties.get(key).remove(property);
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
            globalProperties.get(key).put(property, "");
        }
    }
}
