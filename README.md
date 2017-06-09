# GuiceInjection

Guice Injection allows you to access multiple Guice Binders and Modules across separate archives. Allowing you to configure your applications with injection from multiple dependencies.


Servlets, EJB's, and Stand-Alone is supported. Requires JDK 8.
Tested on TomEE, Tomcat8, Glassfish4, Payara, JBoss5, WildFly.

Current development includes support for MicroProfile Services cross spectrum.

Finally inject across multiple JARS inside of EAR's and WAR's.
Separate your service finder for simple testing, with the full power of Guice on your fingertips!

A java Guice Assistant that allows you to perform multiple and dynamic binding, using org.reflections.

Most Basic Usage Example
```

public class CustomerBinderJar1 extends GuiceDefaultBinder
{
    @Override
    public void onBind(GuiceSiteInjectorModule module)
    {
    }
}
```
Access via @Inject Injector injector or GuiceContext.Injector()


public class JWebSwingSiteBinder extends GuiceSiteBinder


