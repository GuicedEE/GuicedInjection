package za.co.mmagon.guiceinjection.db;

import com.oracle.jaxb21.Persistence;

import java.lang.annotation.Annotation;
import java.util.Properties;

public class TestDBPrivateModule extends AbstractDatabaseProviderModule
{

	@Override
	protected ConnectionBaseInfo getConnectionBaseInfo(Persistence.PersistenceUnit unit, Properties filteredProperties)
	{
		ConnectionBaseInfo cbi = new ConnectionBaseInfo();
		for (String prop : filteredProperties.stringPropertyNames())
		{
			switch (prop)
			{
				case "hibernate.connection.url":
				{
					cbi.setUrl(filteredProperties.getProperty(prop));
					break;
				}
				case "hibernate.connection.user":
				{
					cbi.setUsername(filteredProperties.getProperty(prop));
					break;
				}
				case "hibernate.connection.driver_class":
				{
					cbi.setDriverClass(filteredProperties.getProperty(prop));
					break;
				}
			}
		}
		return cbi;
	}

	@Override
	protected String getJndiMapping()
	{
		return "jdbc/jndi";
	}

	@Override
	protected String getJdbcPropertySuffix()
	{
		return "TestCustomPersistence";
	}

	@Override
	protected String getPersistenceUnitName()
	{
		return "guiceinjectionh2test";
	}

	@Override
	protected Class<? extends Annotation> getBindingAnnotation()
	{
		return TestCustomPersistenceLoader.class;
	}
}
