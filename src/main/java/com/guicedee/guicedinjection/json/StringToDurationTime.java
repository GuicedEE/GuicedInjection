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
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String name = p.getValueAsString();
	    if (Strings.isNullOrEmpty(name) || STRING_NULL.equals(name) || STRING_0.equals(name)) {
		    return null;
	    }
	    if (name.contains(E)) {
		    name = name.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY).substring(0, name.indexOf(E) - 1);
	    }

        if (name.contains(STRING_DOT)) {
            double d = Double.parseDouble(name);
            name = String.valueOf((int) d);
        }

        if(name.length() > 4)
        	return new StringToDurationTimeSeconds().deserialize(p,ctxt);

        name = name.trim();
        if (!name.contains(P)) {
            //Numeric
            if (name.length() < 4) {
                name = StringUtils.leftPad(name, 4, STRING_0);
            }
            int hours = Integer.parseInt(name.substring(0, 2));
            int minutes = Integer.parseInt(name.substring(2));
            return Duration.parse(P + nf.format(hours) + H + nf.format(minutes) + M);
        } else {
            return Duration.parse(name);
        }
    }
}
