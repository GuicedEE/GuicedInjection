package com.guicedee.guicedinjection.json;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToIntegerRelaxed extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return convert(value);
    }

    public Integer convert(@NotNull  String value)
    {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        value = value.trim();
        double d = Double.parseDouble(value);
        return (int) d;
    }
}
