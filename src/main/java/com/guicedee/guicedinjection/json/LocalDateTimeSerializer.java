package com.guicedee.guicedinjection.json;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer
        extends JsonSerializer<LocalDateTime> {
    public static String LocalDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS";

    public LocalDateTimeSerializer() {
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(convert(value));
    }
    
    public String convert(LocalDateTime value)
    {
        if (value == null)
        {
            return null;
        }
        return value.format(DateTimeFormatter.ofPattern(LocalDateTimeFormat));
    }
}
