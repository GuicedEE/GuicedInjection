package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.json.LocalDateTimeDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class LocalDateTimeDeserializationTest
{
	@Test
	public void testLdt() throws IOException
	{
		DateTimeFormatter dt = new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
		                                                     .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)
		                                                     .appendOptional(
				                                                     new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
				                                                                                   .optionalStart()
				                                                                                   .appendLiteral('T')
				                                                                                   .optionalEnd()
				                                                                                   .optionalStart()
				                                                                                   .appendLiteral(' ')
				                                                                                   .optionalEnd()
				                                                                                   .appendPattern("HH:mm:ss")
				                                                                                   .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
				                                                                                   .toFormatter()
		                                                                    )
		                                                     .appendOptional(
				                                                     new DateTimeFormatterBuilder()
						                                                     .appendPattern("yyyy")

						                                                     .optionalStart()
						                                                     .appendLiteral("/")
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendLiteral("-")
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendPattern("MM")
						                                                     .optionalStart()
						                                                     .appendLiteral("/")
						                                                     .optionalEnd()
						                                                     .optionalStart()
						                                                     .appendLiteral("-")
						                                                     .optionalEnd()
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendPattern("dd")
						                                                     .optionalEnd()
						                                                     .optionalStart()
						                                                     .appendPattern("d")
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendLiteral('T')
						                                                     .optionalEnd()
						                                                     .optionalStart()
						                                                     .appendLiteral(' ')
						                                                     .optionalEnd()

						                                                     .optionalStart()

						                                                     .optionalStart()
						                                                     .appendPattern("HH")


						                                                     .optionalStart()
						                                                     .appendLiteral(':')
						                                                     .optionalEnd()
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendPattern("mm")
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendLiteral(':')
						                                                     .optionalEnd()

						                                                     .optionalStart()
						                                                     .appendPattern("ss")
						                                                     .optionalEnd()

						                                                     .optionalEnd()

						                                                     .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
						                                                     .toFormatter()
		                                                                    )
		                                                     .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
		                                                     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
		                                                     .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
		                                                     .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
		                                                     .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
		                                                     .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
		                                                     .toFormatter();
		LocalDateTime ldt = LocalDateTime.parse("2020-05-11T04:59:20.052125", dt);
		LocalDateTime ldt4 = LocalDateTime.parse("2020-05-11 04:59:20.052125", dt);
		LocalDateTime ldt5 = LocalDateTime.parse("2020-05-11T07:53:52.467080", dt);
		LocalDateTime ldt2 = LocalDateTime.parse("2020-05-11T04:59:20.052", dt);
		LocalDateTime ldt3 = LocalDateTime.parse("2020-05-11T04:59:20.052123456", dt);
		LocalDateTime ldt33 = LocalDateTime.parse("2020/05/11T04", dt);
		LocalDateTime ldt43 = LocalDateTime.parse("2020/05/11T04:30", dt);
		LocalDateTime ldt53 = LocalDateTime.parse("2020/05/11T04:30:00", dt);
		LocalDateTime ldt73 = LocalDateTime.parse("2020/05/11 04:30", dt);
		LocalDateTime ldt83 = LocalDateTime.parse("2020/05/11 04:30:00", dt);

		LocalDateTime.parse("2020-05-11", dt);
		LocalDateTime.parse("2020/05/11 04", dt);
		LocalDateTime.parse("2020/05/11", dt);

		//this must be local time -> it must fail
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04:14", dt));
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04:14:20", dt));
		Assertions.assertThrows(DateTimeParseException.class, () -> LocalDateTime.parse("04", dt));

		//direct access conversion
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052125");
		new LocalDateTimeDeserializer().convert("2020-05-11T07:53:52.467080");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052");
		new LocalDateTimeDeserializer().convert("2020-05-11T04:59:20.052123456");

		System.out.println("all parsed");
	}
}
