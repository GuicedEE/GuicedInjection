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
public class StringToBoolean extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        value = value.trim();
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return true;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return false;
        } else if ("1".equalsIgnoreCase(value)) {
            return true;
        } else if ("1.0".equalsIgnoreCase(value)) {
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
        return null;
    }
}
