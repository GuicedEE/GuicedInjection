package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.guicedee.guicedinjection.*;
import com.guicedee.guicedinjection.abstractions.*;
import com.guicedee.guicedinjection.interfaces.*;
import com.guicedee.logger.*;
import jakarta.inject.*;

import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.*;

public class ObjectMapperBinder
		implements IGuiceDefaultBinder<ObjectMapperBinder, GuiceInjectorModule>
{
	/**
	 * Field log
	 */
	private static final java.util.logging.Logger log = LogFactory.getLog("ObjectMapperBinding");
	
	/**
	 * If the object mapper must behave as a singleton
	 */
	public static boolean singleton = true;
	
	/**
	 * Method onBind ...
	 *
	 * @param module of type GuiceInjectorModule
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onBind(GuiceInjectorModule module)
	{
		log.fine("Bound ObjectMapper (DefaultObjectMapper) as singleton [" + singleton+ "]");
		var p = (Provider<ObjectMapper>) () -> new ObjectMapper()
				.registerModule(new LaxJsonModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
				.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
				.enable(ALLOW_UNQUOTED_CONTROL_CHARS)
				.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
				.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
				.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		if(singleton)
		{
			module
					.bind(DefaultObjectMapper)
					.toProvider(p)
					.in(Singleton.class);
		}else {
			module.bind(DefaultObjectMapper)
			      .toProvider(p);
		}
		
		log.fine("Bound ObjectWriter.class @Named(JSON)");
		
		module.bind(ObjectBinderKeys.JSONObjectWriter)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
				                  .writerWithDefaultPrettyPrinter()
				                  .with(SerializationFeature.INDENT_OUTPUT)
				                  .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                  .with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                  .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                  .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		module.bind(ObjectBinderKeys.JSONObjectWriterTiny)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
				                  .writer()
				                  .without(SerializationFeature.INDENT_OUTPUT)
				                  .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                  .with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                  .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                  .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		module.bind(ObjectBinderKeys.JSONObjectReader)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.DefaultObjectMapper)
				                  .reader()
				                  .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				                  .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				                  .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		      );
		
		log.fine("Bound ObjectWriter.class @Named(JavaScriptObjectReader)");
		module.bind(ObjectBinderKeys.JavascriptObjectMapper)
		      .toInstance(ObjectMapperInstance = new ObjectMapper()
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
		
		
		module.bind(JavaScriptObjectWriter)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
				                  .writerWithDefaultPrettyPrinter()
				                  .with(SerializationFeature.INDENT_OUTPUT)
				                  .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                  .without(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                  .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                  .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		module.bind(ObjectBinderKeys.JavaScriptObjectWriterTiny)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
				                  .writer()
				                  .without(SerializationFeature.INDENT_OUTPUT)
				                  .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                  .without(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                  .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                  .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		
		module.bind(ObjectBinderKeys.JavaScriptObjectReader)
		      .toProvider(() ->
				      GuiceContext.get(ObjectBinderKeys.JavascriptObjectMapper)
				                  .reader()
				                  .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				                  .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				                  .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		      );
	}
	
	/**
	 * Returns the object mapper instance
	 * @return
	 */
	public static ObjectMapper getObjectMapper()
	{
		return GuiceContext.get(DefaultObjectMapper);
	}
}
