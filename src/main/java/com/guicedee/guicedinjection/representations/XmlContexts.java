package com.guicedee.guicedinjection.representations;

import jakarta.xml.bind.JAXBContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class XmlContexts {
    public static final Map<Class<?>, JAXBContext> JAXB = new ConcurrentHashMap<>();

    private XmlContexts(){}
}
