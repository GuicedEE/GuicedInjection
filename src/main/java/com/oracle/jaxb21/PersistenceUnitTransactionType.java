
package com.oracle.jaxb21;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "persistence-unit-transaction-type")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public enum PersistenceUnitTransactionType
{

	JTA,
	RESOURCE_LOCAL;

	public static PersistenceUnitTransactionType fromValue(String v)
	{
		return valueOf(v);
	}

	public String value()
	{
		return name();
	}

}
