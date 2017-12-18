package za.co.mmagon.guiceinjection.db;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import za.co.mmagon.guiceinjection.interfaces.CustomPoolDataSource;

import javax.sql.DataSource;

/**
 * Provides the DataSource.
 */
@Singleton
public class JtaPoolDataSource implements Provider<DataSource>, CustomPoolDataSource
{

	private transient DataSource providedDataSource;
	/**
	 * Intentional double assignment
	 */
	private transient PoolingDataSource pds;

	public JtaPoolDataSource()
	{
		//No config required
	}

	@Override
	public void configure(ConnectionBaseInfo cbi)
	{
		if (cbi.isXa())
		{
			processXs(cbi);
		}
		else
		{
			processNonXa(cbi);
		}
	}

	private void processXs(ConnectionBaseInfo cbi)
	{
		pds = new PoolingDataSource();
		if (cbi.getTransactionIsolation() != null)
		{
			pds.setIsolationLevel(cbi.getTransactionIsolation());
		}

		pds.setAllowLocalTransactions(true);
		pds.setUniqueName(cbi.getJndiName());
		pds.setClassName(cbi.getDriverClass());
		pds.setMinPoolSize(5);
		pds.setMaxPoolSize(150);
		pds.setPreparedStatementCacheSize(50);

		if (cbi.getDatabaseName() != null)
		{
			pds.getDriverProperties().setProperty("DatabaseName", cbi.getDatabaseName());
		}
		if (cbi.getUsername() != null)
		{
			pds.getDriverProperties().setProperty("User", cbi.getUsername());
		}
		if (cbi.getPassword() != null)
		{
			pds.getDriverProperties().setProperty("Password", cbi.getPassword());
		}
		if (cbi.getServerName() != null)
		{
			pds.getDriverProperties().setProperty("ServerName", cbi.getServerName());
		}
		if (cbi.getPort() != null)
		{
			pds.getDriverProperties().setProperty("Port", cbi.getPort());
		}
		if (cbi.getInstanceName() != null)
		{
			pds.getDriverProperties().setProperty("ServerName", cbi.getServerName() + "\\" + cbi.getInstanceName());
		}

		pds.init();

		providedDataSource = pds;
	}


	private void processNonXa(ConnectionBaseInfo cbi)
	{
		pds = new PoolingDataSource();
		if (cbi.getTransactionIsolation() != null)
		{
			pds.setIsolationLevel(cbi.getTransactionIsolation());
		}
		pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
		pds.setUniqueName(cbi.getJndiName());
		pds.setMinPoolSize(5);
		pds.setMaxPoolSize(50);
		pds.setAllowLocalTransactions(true);

		if (cbi.getDriverClass() != null)
		{
			pds.getDriverProperties().setProperty("driverClassName", cbi.getDriverClass());
		}
		if (cbi.getUrl() != null)
		{
			pds.getDriverProperties().setProperty("url", cbi.getUrl());
		}
		if (cbi.getUsername() != null)
		{
			pds.getDriverProperties().setProperty("user", cbi.getUsername());
		}
		if (cbi.getPassword() != null)
		{
			pds.getDriverProperties().setProperty("password", cbi.getPassword());
		}
		pds.init();
		providedDataSource = pds;
	}

	@Override
	public DataSource get()
	{
		return providedDataSource;
	}
}
