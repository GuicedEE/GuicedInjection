# Guice Injection

Guice Injection allows you to access multiple Guice Binders and Modules across separate archives as well as run EE functionality in a tiny, super-fast, easy to use MicroServices ready injection framework. That's a lot of adjectives. 

Allowing you to configure your applications with injection from multiple dependencies, and granting you  
Out of the box JTA (Using BTM) and Direct Injection for Request Scoped Transactions, Units of work and complete multi-persistence units compatible with Annotated Qualifiers no other container comes close.

This framework provides you with Servlets, EJB's, JMS, and Stand-Alone  - All Supported. Requires JDK 8.
Tested on TomEE, Tomcat8, Glassfish4, Payara4, JBoss5, WildFly, Undertow and Standalone.

This framework automatically Binds into all the above.

Supports all MicroProfile 1.x.

Finally inject across multiple JARS inside of EAR's and WAR's and run the tests in the exact same manner as if it were actually your production! Even JCache is provided and fully utilized.

Separate your service finder across each module/microservice for simple testing and program development, with the full power of Guice on your fingertips! (https://github.com/google/guice)

A Java Guice Assistant that allows you to perform multiple and dynamic binding, using the fast-classpath-scanner from Luke Hutch (https://github.com/lukehutch/).

```
<repository>
    <snapshots/>
    <id>snapshots</id>
    <name>libs-snapshot</name>
    <url>https://jwebswing.com/artifactory/libs-snapshot</url>
</repository>
```
```
<dependency>
    <groupId>com.jwebmp</groupId>
    <artifactId>guice-injection</artifactId>
    <version>LATEST</version>
</dependency>
```


# Complete CI
Teamcity :  https://jwebswing.com/teamcity/viewType.html?buildTypeId=JWebMPCore_BuildGuiceInjection

SonarQube Quality Gate : https://jwebswing.com/sonar/overview?id=1

Artifactory : https://jwebswing.com/artifactory/webapp/#/artifacts/browse/tree/General/libs-snapshot-local/za/co/mmagon/guice-injection

# Minimums
* JDK 8
* JPA 2.1

This is due to the convertor pattern being standardized in JPA 2.1 as well as a few other things. As an extension of the google-guice DI framework this project uses the latest dependencies available as much as possible.
 
 https://www.thoughts-on-java.org/jpa-21-overview/ 

# Libraries
* Google Guice 4.2 (guice-servlet,guice-persist,guice-jndi,guice-jms)
* Fast-Classpath-Scanner 2.9.4 
* JPA 2.1
* H2 (testing)
* Jackson JSON 2.9.4
* JCache 1.0 (Optional)

### Enabling Package Scanning
Packing scanning allows you to white list the packages that must be scanned for operation. This vastly decreases the search time required in large classpath EE editions. 
By default all Guice Injection libraries white list themselves.
#### Create the service implementation
Package scanning utilizes the Service Loader pattern (https://docs.oracle.com/javase/tutorial/ext/basics/spi.html)

Create a file in the direcory META-INF/services/
za.co.mmagon.guiceinjection.scanners.PackageContentsScanner

Add an entry for your class that implements PackageContentsScanner.

Below is the Guice Injection example

```$xslt
public class GuiceInjectionPackageDefaultInclusions implements PackageContentsScanner
{
	@Override
	public Set<String> searchFor()
	{
		Set<String> strings = new HashSet<>();
		strings.add("META-INF");
		strings.add("za.co.mmagon.guiceinjection");
		return strings;
	}
}
```

### Enabling File Contents Scanning
Guice Injection allows you to specify files that must be scanned on the complete classpath for loading. This becomes very handy for loading configuration injections, finding files on the classpath, and specifying any additional propeties or needs that should occur before Guice even starts up the PreStartup procedures.
Guice Injection uses File Contents Scanning to find any persistence.xml files (JPA2.1) and loads up the guice-persistence extension with the given annotations for these Jar modules

Create a file in the direcory META-INF/services/
za.co.mmagon.guiceinjection.scanners.PackageContentsScanner

Add an entry for your class that implements FileContentsScanner.

Below is the Guice Injection example (That loads the persistence units asynchronously)

```
public class PersistenceFileHandler implements FileContentsScanner, PackageContentsScanner
{
    @Override
	public Map<String, FileMatchContentsProcessorWithContext> onMatch()
	{
		Map<String, FileMatchContentsProcessorWithContext> map = new HashMap<>();

		log.config("Persistence Units Loading... ");
		FileMatchContentsProcessorWithContext processor = (classpathElt, relativePath, fileContents) ->
		{
			log.config("Found " + relativePath + " - " + classpathElt.getCanonicalPath());
			if (!GuiceContext.getAsynchronousPersistenceFileLoader().isShutdown())
			{
				GuiceContext.getAsynchronousPersistenceFileLoader().shutdown();
				try
				{
					GuiceContext.getAsynchronousPersistenceFileLoader().awaitTermination(5, TimeUnit.SECONDS);
				}
				catch (InterruptedException e)
				{
					log.log(Level.SEVERE, "Unable to wait for persistence jaxb context to load..", e);
				}
			}
			persistenceUnits.addAll(getPersistenceUnitFromFile(fileContents));
		};
		map.put("persistence.xml", processor);
		return map;
	}
```
The combination of the package contents scanner and the file contents scanner allows ignoring the com.oracle package for any persistence unit scanning

# Base DI Implementation 
Guice Injection allows you to use the complete library of Google Guice (guice-inject,guice-persist,guice-jms,guice-jndi).
Bindings are specified in classes that are in white-listed packages using either the GuiceDefaultBinder or GuiceSiteBinder. 

```
public class CustomerBinderJar1 extends GuiceDefaultBinder
{
    @Override
    public void onBind(GuiceInjectorModule module)
    {
    }
}

public class CustomeSiteBinderJar1 extends GuiceSiteBinder
{
    @Override
    public void onBind(GuiceSiteInjectorModule module)
    {
    }
}

```
The Guice Site Injector should be specified in WAR files or Servlet 3.0 Enabled Applications, while the Guice Default Binder should be specified for JARS such as EJB's, Client Jar's, and the such. 

# Method Interception (AOP)
You utilize complete AOP provided by Guice. Get a quick and easy handle on annotated development!
See https://github.com/google/guice/wiki/AOP for the complete supported operations
```
module.bindInterceptor$(Matchers.any(), Matchers.annotatedWith(SiteInterception.class),
		                        new SiteIntercepters());
```
# Site & Url Binding
Servlets, Filters and the Servlet 3.x API completely supported using the guice-servlet structure. Paired with the persistence layer you are free to enabled request levelled transactions across multiple persistence units.
The entire guice-servlet api is supported out the box. https://github.com/google/guice/wiki/Servlets
```
module.serveRegex$("(" + JAVASCRIPT_LOCATION + ")" + QUERY_PARAMETERS_REGEX).with(JavaScriptServlet.class);
		log.log(Level.INFO, "Serving JavaScripts at {0}", JAVASCRIPT_LOCATION);
```


# Persistence Unit Bindings 
This framework comes built in with the BTM Transaction Manager, and a complete JPA 2.1 Persistence Unit with *JNDI*. This ensures that your production and your testing environment all run and operate on the same level.
Completely supporting JCache and second level caching. 

