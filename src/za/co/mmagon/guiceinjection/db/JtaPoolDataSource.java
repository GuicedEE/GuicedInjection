package za.co.mmagon.guiceinjection.db;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.sql.DataSource;

/**
 * Provides the DataSource.
 */
@Singleton
public class JtaPoolDataSource implements Provider<DataSource>, CustomPoolDataSource
{

	private DataSource providedDataSource;
	/**
	 * Intentional double assignment
	 */
	private PoolingDataSource pds;

	public JtaPoolDataSource()
	{
	}

	public PoolingDataSource getPds()
	{
		return pds;
	}

	@Override
	public void configure(ConnectionBaseInfo cbi)
	{
		if (cbi.isXa())
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

			pds.getDriverProperties().setProperty("DatabaseName", cbi.getDatabaseName());
			pds.getDriverProperties().setProperty("User", cbi.getUsername());
			pds.getDriverProperties().setProperty("Password", cbi.getPassword());
			pds.getDriverProperties().setProperty("ServerName", cbi.getServerName());

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
		else
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
			pds.getDriverProperties().setProperty("driverClassName", cbi.getDriverClass());
			pds.getDriverProperties().setProperty("url", cbi.getUrl());
			pds.getDriverProperties().setProperty("user", cbi.getUsername());
			pds.getDriverProperties().setProperty("password", cbi.getPassword());
			pds.init();
			providedDataSource = pds;
		}
	}

	@Override
	public DataSource get()
	{
		return providedDataSource;
	}
}
