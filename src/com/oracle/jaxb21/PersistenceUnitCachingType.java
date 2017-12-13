
package com.oracle.jaxb21;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for persistence-unit-caching-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="persistence-unit-caching-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="ALL"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="ENABLE_SELECTIVE"/>
 *     &lt;enumeration value="DISABLE_SELECTIVE"/>
 *     &lt;enumeration value="UNSPECIFIED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "persistence-unit-caching-type")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public enum PersistenceUnitCachingType {

    ALL,
    NONE,
    ENABLE_SELECTIVE,
    DISABLE_SELECTIVE,
    UNSPECIFIED;

    public String value() {
        return name();
    }

    public static PersistenceUnitCachingType fromValue(String v) {
        return valueOf(v);
    }

}
