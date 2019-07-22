package com.jwebmp.guicedinjection.implementations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8JacksonModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeJacksonModule;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedinjection.abstractions.GuiceInjectorModule;
import com.jwebmp.guicedinjection.interfaces.IGuiceDefaultBinder;
import com.jwebmp.logger.LogFactory;

import static com.jwebmp.guicedinjection.interfaces.ObjectBinderKeys.*;

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
	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.install(new ObjectMapperModule());

		module.bind(DefaultObjectMapper)
		      .toInstance(new ObjectMapper()
				                  .registerModule(new JavaTimeJacksonModule())
				                  .registerModule(new Jdk8JacksonModule())
				                  .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				                  .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				                  .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
				                  .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
				                  .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
		                 );

		log.fine("Bound ObjectWriter.class @Named(JSON)");

		module.bind(JSONObjectWriter)
		      .toProvider(() ->
				                  GuiceContext.get(DefaultObjectMapper)
				                              .writerWithDefaultPrettyPrinter()
				                              .with(SerializationFeature.INDENT_OUTPUT)
				                              .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                              .with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                              .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                              .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));

		module.bind(JSONObjectWriterTiny)
		      .toProvider(() ->
				                  GuiceContext.get(DefaultObjectMapper)
				                              .writer()
				                              .without(SerializationFeature.INDENT_OUTPUT)
				                              .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				                              .with(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
				                              .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				                              .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));


		module.bind(JSONObjectReader)
		      .toProvider(() ->
				                  GuiceContext.get(DefaultObjectMapper)
				                              .reader()
				                              .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				                              .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				                              .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		                 );


	}
}
