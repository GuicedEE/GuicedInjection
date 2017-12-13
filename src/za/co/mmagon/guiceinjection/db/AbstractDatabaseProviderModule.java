package za.co.mmagon.guiceinjection.db;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.oracle.jaxb21.Persistence;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.enumerations.FastAccessFileTypes;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract implementation for persistence.exml
 */
public abstract class AbstractDatabaseProviderModule
		extends AbstractModule
{
	private static final Logger log = Logger.getLogger("AbstractDatabaseProviderModule");

	@SuppressWarnings("unchecked")
	public AbstractDatabaseProviderModule()
	{
	}

	protected void configure()
	{
		log.config(getPersistenceUnitName() + " Is Binding");
		Properties jdbcProperties = null;
		jdbcProperties = getJDBCPropertiesMap();
		Persistence.PersistenceUnit pu = getPersistenceUnit();
		if (pu == null)
		{
			return;
		}
		install(new JpaPersistPrivateModule(getPersistenceUnitName(), jdbcProperties, getBindingAnnotation()));
		log.config(getPersistenceUnitName() + " Finished Binding. Please remember to bind the keys");
	}

	@NotNull
	public DataSource provideDataSource(ConnectionBaseInfo cbi)
	{
		JtaPoolDataSource dataSource = new JtaPoolDataSource();
		dataSource.configure(cbi);
		return dataSource.get();
	}

	@NotNull
	public abstract String getPersistenceUnitName();

	@NotNull
	public abstract String getJndiMapping();

	@NotNull
	public abstract String getJdbcPropertySuffix();

	@NotNull
	public Key<DataSource> getDataSourceKey()
	{
		return Key.get(DataSource.class, getBindingAnnotation());
	}

	@NotNull
	public abstract Class<? extends Annotation> getBindingAnnotation();

	@NotNull
	public Key<EntityManager> getEntityManagerKey()
	{
		return Key.get(EntityManager.class, getBindingAnnotation());
	}

	@NotNull
	public Properties getJDBCPropertiesMap()
	{
		Properties jdbcProperties = new Properties();

		Persistence p = getPersistence();
		if (p == null)
		{
			log.severe("Unable to find a persistence unit with name : " + getPersistenceUnitName());
		}
		else
		{
			for (Persistence.PersistenceUnit pu : p.getPersistenceUnit())
			{
				if (pu.getName().equals(getPersistenceUnitName()))
				{
					configurePersistenceUnitProperties(pu, jdbcProperties);
				}
			}
		}
		return jdbcProperties;
	}

	private void configurePersistenceUnitProperties(Persistence.PersistenceUnit pu, Properties jdbcProperties)
	{
		Properties sysProps = System.getProperties();
		for (Persistence.PersistenceUnit.Properties.Property props : pu.getProperties().getProperty())
		{
			String checkProperty = props.getValue().replace("\\$", "");
			checkProperty = checkProperty.replaceAll("\\{", "");
			checkProperty = checkProperty.replaceAll("\\}", "");
			if (sysProps.containsKey(checkProperty))
			{
				jdbcProperties.put(props.getName(), sysProps.get(checkProperty));
			}
			else
			{
				jdbcProperties.put(props.getName(), props.getValue());
			}
		}
	}

	@NotNull
	public Persistence getPersistence()
	{
		if (GuiceContext.getFastAccessFiles().get(FastAccessFileTypes.Persistence).containsKey(getPersistenceUnitName()))
		{
			try
			{
				//Make sure the persistence handler is up and running
				GuiceContext.getAsynchronousPersistenceFileLoader().shutdown();
				GuiceContext.getAsynchronousPersistenceFileLoader().awaitTermination(2, TimeUnit.SECONDS);
				JAXBContext pContext = GuiceContext.getPersistenceContext();
				byte[] contextBytes = GuiceContext.getFastAccessFiles().get(FastAccessFileTypes.Persistence).get(getPersistenceUnitName());
				String content = new String(contextBytes);
				return (Persistence) pContext.createUnmarshaller().unmarshal(new StringReader(content));
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Unable to get the persistence xsd object [" + getPersistenceUnitName() + "]", e);
			}
		}
		return null;
	}

	@NotNull
	public Persistence.PersistenceUnit getPersistenceUnit()
	{
		Persistence p = getPersistence();
		if (p == null)
		{
			log.severe("Unable to get persistence unit!!! - " + getPersistenceUnitName());
			return null;
		}
		for (Persistence.PersistenceUnit pu : p.getPersistenceUnit())
		{
			if (pu.getName().equals(getPersistenceUnitName()))
			{
				return pu;
			}
		}
		return null;
	}

}
