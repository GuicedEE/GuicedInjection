package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.json.LocalDateTimeDeserializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class LocalDateTimeDeserializationTest
{
	@Test
	public void testLdt() throws IOException
	{
		DateTimeFormatter dt = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE)
		                                                     .optionalStart()
		                                                     .appendLiteral('T')
		                                                     .optionalEnd()
		                                                     .optionalStart()
		                                                     .appendLiteral(' ')
		                                                     .optionalEnd()
		                                                     .appendPattern("HH:mm:ss")
		                                                     .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
		                                                     .toFormatter();
		LocalDateTime ldt = LocalDateTime.parse("2020-05-11T04:59:20.052125", dt);
		LocalDateTime ldt4 = LocalDateTime.parse("2020-05-11 04:59:20.052125", dt);
		LocalDateTime ldt5 = LocalDateTime.parse("2020-05-11T07:53:52.467080", dt);
		LocalDateTime ldt2 = LocalDateTime.parse("2020-05-11T04:59:20.052", dt);
		LocalDateTime ldt3 = LocalDateTime.parse("2020-05-11T04:59:20.052123456", dt);

		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052125");
		new LocalDateTimeDeserializer().convert("2020-05-11T07:53:52.467080");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052123456");
		System.out.println("all parsed");
	}
}
