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
	private String jdbcIdentifier;

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
	public ConnectionBaseInfo setXa(boolean xa)
	{
		this.xa = xa;
		return this;
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
	public ConnectionBaseInfo setUrl(String url)
	{
		this.url = url;
		return this;
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
	public ConnectionBaseInfo setServerName(String serverName)
	{
		this.serverName = serverName;
		return this;
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
	public ConnectionBaseInfo setPort(String port)
	{
		this.port = port;
		return this;
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
	public ConnectionBaseInfo setInstanceName(String instanceName)
	{
		this.instanceName = instanceName;
		return this;
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
	public ConnectionBaseInfo setDriver(String driver)
	{
		this.driver = driver;
		return this;
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
	public ConnectionBaseInfo setDriverClass(String driverClass)
	{
		this.driverClass = driverClass;
		return this;
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
	public ConnectionBaseInfo setUsername(String username)
	{
		this.username = username;
		return this;
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
	public ConnectionBaseInfo setPassword(String password)
	{
		this.password = password;
		return this;
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
	public ConnectionBaseInfo setTransactionIsolation(String transactionIsolation)
	{
		this.transactionIsolation = transactionIsolation;
		return this;
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
	public ConnectionBaseInfo setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
		return this;
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
	public ConnectionBaseInfo setJndiName(String jndiName)
	{
		this.jndiName = jndiName;
		return this;
	}

	/**
	 * Gets the jdbc private identifier
	 *
	 * @return
	 */
	public String getJdbcIdentifier()
	{
		return jdbcIdentifier;
	}

	/**
	 * Sets the jdbc private identifier
	 *
	 * @param jdbcIdentifier
	 *
	 * @return
	 */
	public ConnectionBaseInfo setJdbcIdentifier(String jdbcIdentifier)
	{
		this.jdbcIdentifier = jdbcIdentifier;
		return this;
	}
}
