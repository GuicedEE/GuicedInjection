package za.co.mmagon.guiceinjection.enumerations;

/**
 * Returns the file name to end with in order to load.
 * This helps to load names like -test after the primary to override
 */
public enum FastAccessFileTypes
{
	Sql(".sql"),
	Persistence("persistence.xml"),
	WebXml("web.xml"),
	WebFragmentXml("web-fragment.xml"),
	EjbJarXml("ejb-jar.xml"),
	Properties(".properties");

	private String endsWith;

	FastAccessFileTypes(String endsWith)
	{
		this.endsWith = endsWith;
	}

	/**
	 * Returns the file name to end with in order to load.
	 * This helps to load names like -test after the primary to override
	 *
	 * @return
	 */
	public String getEndsWith()
	{
		return endsWith;
	}

	public void setEndsWith(String endsWith)
	{
		this.endsWith = endsWith;
	}
}
