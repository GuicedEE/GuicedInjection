
package com.oracle.jaxb21;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for persistence-unit-transaction-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="persistence-unit-transaction-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="JTA"/>
 *     &lt;enumeration value="RESOURCE_LOCAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "persistence-unit-transaction-type")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public enum PersistenceUnitTransactionType {

    JTA,
    RESOURCE_LOCAL;

    public String value() {
        return name();
    }

    public static PersistenceUnitTransactionType fromValue(String v) {
        return valueOf(v);
    }

}
