package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.inject.AbstractModule;
import com.guicedee.guicedinjection.*;
import com.guicedee.guicedinjection.abstractions.*;
import com.guicedee.guicedinjection.interfaces.*;
import jakarta.inject.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;

import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.*;

@Log
public class ObjectMapperBinder
				extends AbstractModule
				implements IGuiceModule<ObjectMapperBinder>
{
	
	/**
	 * If the object mapper must behave as a singleton
	 */
	public static boolean singleton = true;
	
	/**
	 * Method onBind ...
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void configure()
	{
		log.config("Bound ObjectMapper (DefaultObjectMapper) as singleton [" + singleton + "]");
		var p = (Provider<ObjectMapper>) () -> new ObjectMapper()
						.registerModule(new JavaTimeModule())
						//.registerModule(new LaxJsonModule())
						.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
						.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
						.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
						.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
						.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
						.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
						.enable(ALLOW_UNQUOTED_CONTROL_CHARS)
						.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
						.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
						.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
						.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
						.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		if (singleton)
		{
			bind(DefaultObjectMapper)
							.toProvider(p)
							.in(Singleton.class);
		} else
		{
			bind(DefaultObjectMapper)
							.toProvider(p);
		}
		
		log.fine("Bound ObjectWriter.class @Named(JSON)");
		
		bind(ObjectBinderKeys.JSONObjectWriter)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
														.writerWithDefaultPrettyPrinter()
														.with(SerializationFeature.INDENT_OUTPUT)
														.with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
														.with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
														.without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
														.withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		bind(ObjectBinderKeys.JSONObjectWriterTiny)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
														.writer()
														.without(SerializationFeature.INDENT_OUTPUT)
														.with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
														.with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
														.without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
														.withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		bind(ObjectBinderKeys.JSONObjectReader)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
														.reader()
														.with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
														.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
														.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
						);
		
		log.fine("Bound ObjectWriter.class @Named(JavaScriptObjectReader)");
		bind(ObjectBinderKeys.JavascriptObjectMapper)
						.toInstance(new ObjectMapper()
										.registerModule(new LaxJsonModule())
										.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
										.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
										.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
										.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
										.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
										.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
										.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false)
										.enable(ALLOW_UNQUOTED_CONTROL_CHARS)
										.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
										.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
										.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
										.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
						);
		
		
		bind(JavaScriptObjectWriter)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
														.writerWithDefaultPrettyPrinter()
														.with(SerializationFeature.INDENT_OUTPUT)
														.with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
														.without(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
														.without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
														.withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		bind(ObjectBinderKeys.JavaScriptObjectWriterTiny)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
														.writer()
														.without(SerializationFeature.INDENT_OUTPUT)
														.with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
														.without(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
														.without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
														.withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		bind(ObjectBinderKeys.JavaScriptObjectReader)
						.toProvider(() ->
										GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
														.reader()
														.with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
														.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
														.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
						);
	}
	
	/**
	 * Returns the object mapper instance for JAVASCRIPT
	 *
	 * @return
	 */
	public static ObjectMapper getObjectMapper()
	{
		return GuiceContext.get(DefaultObjectMapper);
	}
}
