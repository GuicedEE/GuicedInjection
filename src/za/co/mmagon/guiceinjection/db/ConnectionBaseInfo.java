package za.co.mmagon.guiceinjection.db;

import java.io.Serializable;

/**
 * This class is a container for the database jtm builder string
 */
public class ConnectionBaseInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
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

	/**
	 * If the connection is an XA resource
	 *
	 * @return
	 */
	public boolean isXa()
	{
		return xa;
	}

	/**
	 * If the connection ins an XA Resource
	 *
	 * @param xa
	 */
	public void setXa(boolean xa)
	{
		this.xa = xa;
	}

	/**
	 * Returns a provided URL
	 *
	 * @return
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets a provided URL
	 *
	 * @param url
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Returns the server name
	 *
	 * @return
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * Sets the server name
	 *
	 * @param serverName
	 */
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	/**
	 * Returns the port
	 *
	 * @return
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * Sets the port
	 *
	 * @param port
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * Gets the instance name
	 *
	 * @return
	 */
	public String getInstanceName()
	{
		return instanceName;
	}

	/**
	 * Sets the instance name
	 *
	 * @param instanceName
	 */
	public void setInstanceName(String instanceName)
	{
		this.instanceName = instanceName;
	}

	/**
	 * Gets a driver
	 *
	 * @return
	 */
	public String getDriver()
	{
		return driver;
	}

	/**
	 * Sets a driver
	 *
	 * @param driver
	 */
	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	/**
	 * Gets a driver class
	 *
	 * @return
	 */
	public String getDriverClass()
	{
		return driverClass;
	}

	/**
	 * Sets a driver class
	 *
	 * @param driverClass
	 */
	public void setDriverClass(String driverClass)
	{
		this.driverClass = driverClass;
	}

	/**
	 * Gets a username
	 *
	 * @return
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets a user name
	 *
	 * @param username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Gets a password
	 *
	 * @return
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets a password
	 *
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Gets the transaction isolation
	 *
	 * @return
	 */
	public String getTransactionIsolation()
	{
		return transactionIsolation;
	}

	/**
	 * Sets the transaction isolation
	 *
	 * @param transactionIsolation
	 */
	public void setTransactionIsolation(String transactionIsolation)
	{
		this.transactionIsolation = transactionIsolation;
	}

	/**
	 * Gets the database name
	 *
	 * @return
	 */
	public String getDatabaseName()
	{
		return databaseName;
	}

	/**
	 * Sets the database name
	 *
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}

	/**
	 * Gets the jndi name
	 *
	 * @return
	 */
	public String getJndiName()
	{
		return jndiName;
	}

	/**
	 * Sets the jndi name
	 *
	 * @param jndiName
	 */
	public void setJndiName(String jndiName)
	{
		this.jndiName = jndiName;
	}
}
