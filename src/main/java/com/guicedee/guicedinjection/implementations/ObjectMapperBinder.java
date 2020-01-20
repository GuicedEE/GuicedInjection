package com.guicedee.guicedinjection.implementations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.abstractions.GuiceInjectorModule;
import com.guicedee.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.guicedee.guicedinjection.interfaces.ObjectBinderKeys;
import com.guicedee.guicedinjection.interfaces.LocalDateTimeDeserializer;
import com.guicedee.guicedinjection.interfaces.LocalDateTimeSerializer;
import com.guicedee.logger.LogFactory;

import java.time.LocalDateTime;

public class ObjectMapperBinder
		implements IGuiceDefaultBinder<ObjectMapperBinder, GuiceInjectorModule>
{
	/**
	 * Field log
	 */
	private static final java.util.logging.Logger log = LogFactory.getLog("ObjectMapperBinding");

	/**
	 * Method onBind ...
	 *
	 * @param module
	 * 		of type GuiceInjectorModule
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.install(new ObjectMapperModule());

		SimpleModule sm = new SimpleModule("LocalDateTime", Version.unknownVersion());
		sm.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer());
		sm.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer());

		module.bind(ObjectBinderKeys.DefaultObjectMapper)
		      .toInstance(new ObjectMapper()
				                  .registerModule(new Jdk8Module())
				                  .registerModule(sm)
				                  .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				                  .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				                  .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
				                  .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
				                  .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
		                 );

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
	}
}
