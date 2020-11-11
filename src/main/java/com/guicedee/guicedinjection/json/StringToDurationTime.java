package com.guicedee.guicedinjection.json;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

import static com.guicedee.guicedinjection.json.StaticStrings.*;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToDurationTime extends JsonDeserializer<Duration> {
    private static final NumberFormat nf = NumberFormat.getInstance();

    static {
        nf.setMinimumIntegerDigits(2);
    }

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String name = p.getValueAsString();
        return convert(name);
    }

    public Duration convert(@NotNull String value)
    {
        if (Strings.isNullOrEmpty(value) || STRING_NULL.equals(value) || STRING_0.equals(value)) {
            return null;
        }
        if (value.contains(E)) {
            value = value.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY).substring(0, value.indexOf(E) - 1);
        }

        if (value.contains(STRING_DOT)) {
            double d = Double.parseDouble(value);
            value = String.valueOf((int) d);
        }

        if(value.length() > 4)
            return new StringToDurationTimeSeconds().convert(value);

        value = value.trim();
        if (!value.contains(P)) {
            //Numeric
            if (value.length() < 4) {
                value = StringUtils.leftPad(value, 4, STRING_0);
            }
            int hours = Integer.parseInt(value.substring(0, 2));
            int minutes = Integer.parseInt(value.substring(2));
            return Duration.parse(P + nf.format(hours) + H + nf.format(minutes) + M);
        } else {
            return Duration.parse(value);
        }
    }
}
