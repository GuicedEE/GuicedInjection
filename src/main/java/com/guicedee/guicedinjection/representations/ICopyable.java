package com.guicedee.guicedinjection.representations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.logger.LogFactory;

import java.io.IOException;
import java.util.logging.Level;

public interface ICopyable<J> {
    default J updateFrom(Object source) {
        ObjectMapper om = GuiceContext.get(Key.get(ObjectMapper.class, Names.named("Default")));
        try {
            String jsonFromSource = om.writeValueAsString(source);
            om.readerForUpdating(this).readValue(jsonFromSource);
        } catch (IOException e) {
            LogFactory.getLog("ICopyable").log(Level.SEVERE, "Cannot write or read source/destination", e);
        }
        return (J) this;
    }
}
