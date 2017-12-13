package za.co.mmagon.guiceinjection.db;

/**
 * This class is a container for the database jtm builder string
 */
public class ConnectionBaseInfo
{
	private boolean xa;
	private String url;
	private String serverName;
	private String port;
	private String instanceName;
	private String driver;
	private String driverClass;
	private String username;
	private String password;
	private String transactionIsolation;
	private String databaseName;
	private String jndiName;

	public ConnectionBaseInfo()
	{
		//No config needed
	}

	public boolean isXa()
	{
		return xa;
	}

	public void setXa(boolean xa)
	{
		this.xa = xa;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public String getInstanceName()
	{
		return instanceName;
	}

	public void setInstanceName(String instanceName)
	{
		this.instanceName = instanceName;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getDriverClass()
	{
		return driverClass;
	}

	public void setDriverClass(String driverClass)
	{
		this.driverClass = driverClass;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getTransactionIsolation()
	{
		return transactionIsolation;
	}

	public void setTransactionIsolation(String transactionIsolation)
	{
		this.transactionIsolation = transactionIsolation;
	}

	public String getDatabaseName()
	{
		return databaseName;
	}

	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}

	public String getJndiName()
	{
		return jndiName;
	}

	public void setJndiName(String jndiName)
	{
		this.jndiName = jndiName;
	}
}
