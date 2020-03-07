package com.guicedee.guicedinjection.json;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Converts most of the string knowns to boolean
 */
public class StringToBool extends JsonDeserializer {
    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        value = value.trim();
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return true;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return false;
        } else if ("1".equalsIgnoreCase(value)) {
            return true;
        } else if ("0".equalsIgnoreCase(value)) {
            return false;
        } else if ("Y".equalsIgnoreCase(value)) {
            return true;
        } else if ("N".equalsIgnoreCase(value)) {
            return false;
        } else if ("Yes".equalsIgnoreCase(value)) {
            return true;
        } else if ("No".equalsIgnoreCase(value)) {
            return false;
        }
        return false;
    }
}
