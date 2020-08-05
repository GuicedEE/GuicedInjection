package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.json.LocalDateTimeDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;

import static com.guicedee.guicedinjection.json.LocalDateTimeDeserializer.formats;

public class LocalDateTimeDeserializationTest
{
	@Test
	public void testLdt() throws IOException
	{

		LocalDateTime ldt = LocalDateTime.parse("2020-05-11T04:59:20.052125", formats[4]);
		LocalDateTime ldt4 = LocalDateTime.parse("2020-05-11 04:59:20.052125", formats[4]);
		LocalDateTime ldt5 = LocalDateTime.parse("2020-05-11T07:53:52.467080", formats[4]);
		LocalDateTime ldt2 = LocalDateTime.parse("2020-05-11T04:59:20.052", formats[4]);
		LocalDateTime ldt3 = LocalDateTime.parse("2020-05-11T04:59:20.052123456", formats[4]);
		LocalDateTime ldt33 = LocalDateTime.parse("2020/05/11T04", formats[4]);
		LocalDateTime ldt43 = LocalDateTime.parse("2020/05/11T04:30", formats[4]);
		LocalDateTime ldt53 = LocalDateTime.parse("2020/05/11T04:30:00", formats[4]);
		LocalDateTime ldt73 = LocalDateTime.parse("2020/05/11 04:30", formats[4]);
		LocalDateTime ldt83 = LocalDateTime.parse("2020/05/11 04:30:00", formats[4]);

		LocalDateTime.parse("2020-05-11", formats[4]);
		LocalDateTime.parse("2020/05/11 04", formats[4]);
		LocalDateTime.parse("2020/05/11", formats[4]);

		//this must be local time -> it must fail
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04:14", formats[4]));
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04:14:20", formats[4]));
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04", formats[4]));

		//direct access conversion
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052125");
		new LocalDateTimeDeserializer().convert("2020-05-11T07:53:52.467080");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052");
		new LocalDateTimeDeserializer().convert("2016-10-02T20:15:30-06:00");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052+01:00");
		new LocalDateTimeDeserializer().convert("2016-12-02T11:15:30-05:00");
		new LocalDateTimeDeserializer().convert("2016-12-02T11:15:30Z");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052123456");

		System.out.println("all parsed");
	}
}
