package com.guicedee.guicedinjection.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.guicedee.guicedinjection.json.StaticStrings.*;


public class LocalDateDeserializer
        extends JsonDeserializer<LocalDate> {
    private static final NumberFormat eFormatter = new DecimalFormat("0.000000E0");

    public static String LocalDateTimeFormat = "yyyy-MM-dd";
    public static String LocalDateTimeFormat2 = "yyyyMMdd";
    public static String LocalDateTimeFormat3 = "yyyy/MM/dd";
    public static String LocalDateTimeFormat4 = "yyyyMMd";

    private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
            {DateTimeFormatter.ofPattern(LocalDateTimeFormat),
                    DateTimeFormatter.ofPattern(LocalDateTimeFormat2),
                    DateTimeFormatter.ofPattern(LocalDateTimeFormat3),
                    DateTimeFormatter.ofPattern(LocalDateTimeFormat4)
            };

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String name = p.getValueAsString();
        if (Strings.isNullOrEmpty(name) || STRING_NULL.equals(name) || STRING_0.equals(name)) {
            return null;
        }
        if (name.contains(E)) {
            name = name.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY).substring(0, name.indexOf(E) - 1);
        }
        if (name.length() == 7) {
            name = new StringBuilder(name).insert(name.length() - 1, 0).toString();
        }
        LocalDate time = null;
        for (DateTimeFormatter format : formats) {
            try {
                time = LocalDate.parse(name, format);
                break;
            } catch (DateTimeParseException dtpe) {
                //try the next one
            }
        }
        if (time == null) {
            throw new IOException("Unable to determine local date from string - " + name);
        }
        return time;
    }
}
