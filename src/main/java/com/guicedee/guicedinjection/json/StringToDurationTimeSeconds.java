package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToDurationTimeSeconds extends JsonDeserializer<Duration> {
    private static final NumberFormat nf = NumberFormat.getInstance();

    static {
        nf.setMinimumIntegerDigits(2);
    }

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (Strings.isNullOrEmpty(value) || "NULL".equals(value) || "0".equals(value)) {
            return null;
        }
        if (value.contains("E")) {
            value = value.replaceAll("\\.", "").substring(0, value.indexOf('E') - 1);
        }

        if (value.contains(".")) {
            Double d = Double.parseDouble(value);
            value = String.valueOf(d.intValue());
        }

        value = value.trim();
        if (value.length() < 6) {
            value = StringUtils.leftPad(value, value.length() + 1, '0');
        }
        if (value.indexOf('P') < 0) {
            //Numeric
            int hours = Integer.parseInt(value.substring(0, 2));
            int minutes = Integer.parseInt(value.substring(2, 4));
            int seconds = Integer.parseInt(value.substring(4, 6));
            return Duration.parse("PT" + nf.format(hours) + "H" + nf.format(minutes) + "M" + nf.format(seconds) + "S");
        } else {
            return Duration.parse(value);
        }
    }
}
