package com.guicedee.guicedinjection.json;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

import static com.guicedee.guicedinjection.json.StaticStrings.*;
import static java.time.temporal.ChronoUnit.*;

/**
 * Converts duration to an integer with each portion forced as a single digit
 */
public class DurationToInteger
		extends JsonSerializer<Duration>
{
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	static {
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setMinimumIntegerDigits(1);
	}
	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException
	{
		if(value == null)
			return ;

		String intNumber = numberFormat.format(value.get(HOURS)) + STRING_EMPTY +
		                   numberFormat.format(value.get(MINUTES)) + STRING_EMPTY +
		                   numberFormat.format(value.get(SECONDS));
		gen.writeNumber(Integer.parseInt(intNumber));
	}
}
