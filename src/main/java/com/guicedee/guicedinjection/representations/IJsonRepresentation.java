package com.guicedee.guicedinjection.representations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.exceptions.JsonRenderException;
import com.guicedee.guicedinjection.interfaces.ObjectBinderKeys;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.*;

@SuppressWarnings("unused")
public interface IJsonRepresentation<J> extends ICopyable<J>
{
	/**
	 * Serializes this object as JSON
	 *
	 * @return The rendered JSON or an empty string
	 */
	default String toJson()
	{
		return toJson(false);
	}
	
	/**
	 * Serializes this object as JSON
	 *
	 * @return The rendered JSON or an empty string
	 */
	default String toJson(boolean tiny)
	{
		ObjectMapper objectMapper = GuiceContext.get(DefaultObjectMapper);
		try
		{
			if (tiny)
			{
				return objectMapper.disable(SerializationFeature.INDENT_OUTPUT)
				                   .writeValueAsString(this);
			}
			else
			{
				return objectMapper.writerWithDefaultPrettyPrinter()
				                   .writeValueAsString(this);
			}
		}
		catch (JsonProcessingException e)
		{
			throw new JsonRenderException("Unable to serialize as JSON", e);
		}
	}
	
	/**
	 * Deserializes this object from a JSON String (updates the current object)
	 *
	 * @param json The JSON String
	 * @return This object updated
	 */
	default J fromJson(String json)
	{
		ObjectMapper objectMapper = GuiceContext.get(DefaultObjectMapper);
		try
		{
			return objectMapper.readerForUpdating(this)
			                   .readValue(json);
		}
		catch (IOException e)
		{
			throw new JsonRenderException("Unable to serialize as JSON", e);
		}
	}
	
	/**
	 * Deserializes this object from a JSON String (updates the current object)
	 *
	 * @param json The JSON String
	 * @return This object updated
	 */
	@SuppressWarnings({"UnusedReturnValue"})
	default List<J> fromJsonArray(String json)
	{
		ObjectMapper objectMapper = GuiceContext.get(DefaultObjectMapper);
		try
		{
			return objectMapper.readerFor(new TypeReference<List<J>>() {})
			                   .readValue(json);
		}
		catch (IOException e)
		{
			throw new JsonRenderException("Unable to serialize as JSON", e);
		}
	}
	
	/**
	 * Deserializes this object from a JSON String (updates the current object)
	 *
	 * @param json The JSON String
	 * @return This object updated
	 */
	@SuppressWarnings({"UnusedReturnValue"})
	default Set<J> fromJsonArrayUnique(String json, @SuppressWarnings("unused")
			Class<J> type)
	{
		ObjectMapper objectMapper = GuiceContext.get(DefaultObjectMapper);
		try
		{
			return objectMapper.readerFor(new TypeReference<TreeSet<J>>() {})
			                   .readValue(json);
		}
		catch (IOException e)
		{
			throw new JsonRenderException("Unable to serialize as JSON", e);
		}
	}
	
	/**
	 * Read direct from the stream
	 *
	 * @param <T>
	 * @param file  the stream
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> T From(InputStream file, Class<T> clazz) throws IOException
	{
		return getJsonObjectReader().forType(clazz)
		                            .readValue(file);
	}
	
	
	/**
	 * Read from a file
	 *
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> T From(File file, Class<T> clazz) throws IOException
	{
		return getJsonObjectReader().forType(clazz)
		                            .readValue(file);
	}
	
	/**
	 * Read from a reader
	 *
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> T From(Reader file, Class<T> clazz) throws IOException
	{
		return getJsonObjectReader().forType(clazz)
		                            .readValue(file);
	}
	
	static ObjectReader getJsonObjectReader()
	{
		return GuiceContext.get(ObjectBinderKeys.JSONObjectReader);
	}
	
	/**
	 * Read from a content string
	 *
	 * @param <T>
	 * @param content
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> T From(String content, Class<T> clazz) throws IOException
	{
		return getJsonObjectReader().forType(clazz)
		                            .readValue(content);
	}
	
	/**
	 * Read from a URL
	 *
	 * @param <T>
	 * @param content
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> T From(URL content, Class<T> clazz) throws IOException
	{
		return getJsonObjectReader().forType(clazz)
		                            .readValue(content);
	}
	
	
	/**
	 * Read direct from the stream
	 *
	 * @param <T>
	 * @param file  the stream
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> List<T> fromToList(InputStream file, Class<T> clazz)
	{
		T list = null;
		try
		{
			list = GuiceContext.get(DefaultObjectMapper)
			                   .reader()
			                   .forType(clazz)
			                   .readValue(file);
		}
		catch (IOException e)
		{
			throw new JsonRenderException("Unable to read the input stream ", e);
		}
		ArrayList<T> lists = new ArrayList<>();
		lists.addAll(Arrays.asList((T[]) list));
		return lists;
	}
	
	/**
	 * Read from a URL
	 *
	 * @param <T>
	 * @param content
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> List<T> fromToList(URL content, Class<T> clazz) throws IOException
	{
		T list = GuiceContext.get(DefaultObjectMapper)
		                     .reader()
		                     .forType(clazz)
		                     .readValue(content);
		ArrayList<T> lists = new ArrayList<>();
		lists.addAll(Arrays.asList((T[]) list));
		return lists;
	}
	
	/**
	 * Read from a file
	 *
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> List<T> fromToList(File file, Class<T> clazz) throws IOException
	{
		T list = GuiceContext.get(DefaultObjectMapper)
		                     .reader()
		                     .forType(clazz)
		                     .readValue(file);
		ArrayList<T> lists = new ArrayList<>();
		lists.addAll(Arrays.asList((T[]) list));
		return lists;
	}
	
	/**
	 * Read from a reader
	 *
	 * @param <T>
	 * @param file
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> List<T> fromToList(Reader file, Class<T> clazz) throws IOException
	{
		T list = GuiceContext.get(DefaultObjectMapper)
		                     .reader()
		                     .forType(clazz)
		                     .readValue(file);
		ArrayList<T> lists = new ArrayList<>();
		lists.addAll(Arrays.asList((T[]) list));
		return lists;
	}
	
	/**
	 * Read from a content string
	 *
	 * @param <T>
	 * @param content
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	static <T> List<T> fromToList(String content, Class<T> clazz) throws IOException
	{
		T list = GuiceContext.get(DefaultObjectMapper)
		                     .reader()
		                     .forType(clazz)
		                     .readValue(content);
		ArrayList<T> lists = new ArrayList<>();
		lists.addAll(Arrays.asList((T[]) list));
		return lists;
	}
	
}
