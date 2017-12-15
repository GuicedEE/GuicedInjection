package com.oracle.jaxb21;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"persistenceUnit"
})
@XmlRootElement(name = "persistence", namespace = "http://xmlns.jcp.org/xml/ns/persistence")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Persistence
{

	@XmlElement(name = "persistence-unit", required = true)
	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	protected List<Persistence.PersistenceUnit> persistenceUnit;
	@XmlAttribute(name = "version", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	protected String version;

	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	public List<Persistence.PersistenceUnit> getPersistenceUnit()
	{
		if (persistenceUnit == null)
		{
			persistenceUnit = new ArrayList<Persistence.PersistenceUnit>();
		}
		return this.persistenceUnit;
	}


	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	public String getVersion()
	{
		if (version == null)
		{
			return "2.1";
		}
		else
		{
			return version;
		}
	}

	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	public void setVersion(String value)
	{
		this.version = value;
	}


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"description",
			"provider",
			"jtaDataSource",
			"nonJtaDataSource",
			"mappingFile",
			"jarFile",
			"clazz",
			"excludeUnlistedClasses",
			"sharedCacheMode",
			"validationMode",
			"properties"
	})
	@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
	public static class PersistenceUnit
	{

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected String description;
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected String provider;
		@XmlElement(name = "jta-data-source")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected String jtaDataSource;
		@XmlElement(name = "non-jta-data-source")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected String nonJtaDataSource;
		@XmlElement(name = "mapping-file")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected List<String> mappingFile;
		@XmlElement(name = "jar-file")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected List<String> jarFile;
		@XmlElement(name = "class")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected List<String> clazz;
		@XmlElement(name = "exclude-unlisted-classes", defaultValue = "true")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected Boolean excludeUnlistedClasses;
		@XmlElement(name = "shared-cache-mode")
		@XmlSchemaType(name = "token")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected PersistenceUnitCachingType sharedCacheMode;
		@XmlElement(name = "validation-mode")
		@XmlSchemaType(name = "token")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected PersistenceUnitValidationModeType validationMode;
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected Persistence.PersistenceUnit.Properties properties;
		@XmlAttribute(name = "name", required = true)
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected String name;
		@XmlAttribute(name = "transaction-type")
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		protected PersistenceUnitTransactionType transactionType;

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public String getDescription()
		{
			return description;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setDescription(String value)
		{
			this.description = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public String getProvider()
		{
			return provider;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setProvider(String value)
		{
			this.provider = value;
		}


		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public String getJtaDataSource()
		{
			return jtaDataSource;
		}


		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setJtaDataSource(String value)
		{
			this.jtaDataSource = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public String getNonJtaDataSource()
		{
			return nonJtaDataSource;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setNonJtaDataSource(String value)
		{
			this.nonJtaDataSource = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public List<String> getMappingFile()
		{
			if (mappingFile == null)
			{
				mappingFile = new ArrayList<String>();
			}
			return this.mappingFile;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public List<String> getJarFile()
		{
			if (jarFile == null)
			{
				jarFile = new ArrayList<String>();
			}
			return this.jarFile;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public List<String> getClazz()
		{
			if (clazz == null)
			{
				clazz = new ArrayList<String>();
			}
			return this.clazz;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public Boolean isExcludeUnlistedClasses()
		{
			return excludeUnlistedClasses;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setExcludeUnlistedClasses(Boolean value)
		{
			this.excludeUnlistedClasses = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public PersistenceUnitCachingType getSharedCacheMode()
		{
			return sharedCacheMode;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setSharedCacheMode(PersistenceUnitCachingType value)
		{
			this.sharedCacheMode = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public PersistenceUnitValidationModeType getValidationMode()
		{
			return validationMode;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setValidationMode(PersistenceUnitValidationModeType value)
		{
			this.validationMode = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public Persistence.PersistenceUnit.Properties getProperties()
		{
			return properties;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setProperties(Persistence.PersistenceUnit.Properties value)
		{
			this.properties = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public String getName()
		{
			return name;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setName(String value)
		{
			this.name = value;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public PersistenceUnitTransactionType getTransactionType()
		{
			return transactionType;
		}

		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public void setTransactionType(PersistenceUnitTransactionType value)
		{
			this.transactionType = value;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = {
				"property"
		})
		@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
		public static class Properties
		{

			@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
			protected List<Persistence.PersistenceUnit.Properties.Property> property;

			@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
			public List<Persistence.PersistenceUnit.Properties.Property> getProperty()
			{
				if (property == null)
				{
					property = new ArrayList<Persistence.PersistenceUnit.Properties.Property>();
				}
				return this.property;
			}

			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "")
			@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
			public static class Property
			{

				@XmlAttribute(name = "name", required = true)
				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				protected String name;
				@XmlAttribute(name = "value", required = true)
				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				protected String value;


				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				public String getName()
				{
					return name;
				}


				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				public void setName(String value)
				{
					this.name = value;
				}


				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				public String getValue()
				{
					return value;
				}


				@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2017-11-21T09:46:20+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
				public void setValue(String value)
				{
					this.value = value;
				}

			}

		}

	}

}
