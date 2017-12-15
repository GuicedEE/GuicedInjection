package za.co.mmagon.guiceinjection.db;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.oracle.jaxb21.Persistence;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.annotations.GuiceInjectorModule;
import za.co.mmagon.guiceinjection.enumerations.FastAccessFileTypes;
import za.co.mmagon.guiceinjection.exceptions.NoConnectionInfoException;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract implementation for persistence.exml
 */
@GuiceInjectorModule
public abstract class AbstractDatabaseProviderModule
		extends AbstractModule
{
	protected static final Set<Persistence> globalUnits = new LinkedHashSet<>();
	private static final Logger log = Logger.getLogger("AbstractDatabaseProviderModule");

	@SuppressWarnings("unchecked")
	public AbstractDatabaseProviderModule()
	{
	}

	/**
	 * Builds up connection base data info from a persistence unit.
	 * <p>
	 * Use with the utility methods e.g.
	 *
	 * @param unit
	 *
	 * @return
	 */
	@NotNull
	protected abstract ConnectionBaseInfo getConnectionBaseInfo(Persistence.PersistenceUnit unit, Properties filteredProperties);

	/**
	 * The name found in jta-data-source from the persistence.xml
	 *
	 * @return
	 */
	@NotNull
	protected abstract String getJndiMapping();

	/**
	 * A unique suffix to apply to the binding names
	 *
	 * @return
	 */
	@NotNull
	protected abstract String getJdbcPropertySuffix();

	/**
	 * The name found in persistence.xml
	 *
	 * @return
	 */
	@NotNull
	protected abstract String getPersistenceUnitName();

	/**
	 * Configures the module with the bindings
	 */
	protected void configure()
	{
		log.config(getPersistenceUnitName() + " Is Binding");
		loadPersistenceUnits();
		Properties jdbcProperties = null;
		jdbcProperties = getJDBCPropertiesMap();
		Persistence.PersistenceUnit pu = getPersistenceUnit();
		if (pu == null)
		{
			log.severe("Unable to register persistence unit with name " + getPersistenceUnitName() + " - No persistence unit containing this name was found.");
			return;
		}
		install(new JpaPersistPrivateModule(getPersistenceUnitName(), jdbcProperties, getBindingAnnotation()));
		final ConnectionBaseInfo connectionBaseInfo = getConnectionBaseInfo(pu, jdbcProperties);
		connectionBaseInfo.setJndiName(getJndiMapping());
		connectionBaseInfo.setJdbcIdentifier(getJdbcPropertySuffix());
		bind(Key.get(DataSource.class, getBindingAnnotation())).toProvider(() -> provideDataSource(connectionBaseInfo));
		log.config(getPersistenceUnitName() + " Finished Binding. Please remember to bind the keys");
	}

	/**
	 * A properties map of the properties from the file
	 *
	 * @return
	 */
	@NotNull
	private Properties getJDBCPropertiesMap()
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

	@NotNull
	protected Key<DataSource> getDataSourceKey()
	{
		return Key.get(DataSource.class, getBindingAnnotation());
	}

	/**
	 * The annotation which will identify this guy
	 *
	 * @return
	 */
	@NotNull
	protected abstract Class<? extends Annotation> getBindingAnnotation();

	@NotNull
	private DataSource provideDataSource(ConnectionBaseInfo cbi)
	{
		if (cbi == null)
		{
			throw new NoConnectionInfoException("Not point in trying to create a connection with no info.....");
		}
		JtaPoolDataSource dataSource = new JtaPoolDataSource();
		dataSource.configure(cbi);
		return dataSource.get();
	}

	private void loadPersistenceUnits()
	{
		if (globalUnits.isEmpty())
		{
			Map<String, byte[]> me = GuiceContext.getFastAccessFiles().get(FastAccessFileTypes.Persistence);
			me.forEach((key, value) ->
			           {
				           JAXBContext pContext = GuiceContext.getPersistenceContext();
				           String content = new String(value);
				           try
				           {
					           globalUnits.add((Persistence) pContext.createUnmarshaller().unmarshal(new StringReader(content)));
				           }
				           catch (JAXBException e)
				           {
					           log.log(Level.SEVERE, "Unable to get the persistence xsd object [" + getPersistenceUnitName() + "]", e);
				           }
			           });
		}
	}

	/**
	 * Returns the persistence file associated with this module
	 *
	 * @return
	 */
	private Persistence getPersistence()
	{
		for (Persistence a : globalUnits)
		{
			for (Persistence.PersistenceUnit b : a.getPersistenceUnit())
			{
				if (b.getName().equals(getPersistenceUnitName()))
				{
					return a;
				}
			}
		}
		return null;
	}

	/**
	 * Builds a property map from a persistence unit properties file
	 *
	 * @param pu
	 * @param jdbcProperties
	 */
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

	/**
	 * Returns the persistence unit associated with the supplied name
	 *
	 * @return
	 */
	private Persistence.PersistenceUnit getPersistenceUnit()
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
