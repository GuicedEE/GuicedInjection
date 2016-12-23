# GuiceInjection
Finally inject across multiple JARS inside of EAR's and WAR's
A java Guice Assistant that allows you to perform binding in multiple JAR's using org.reflections.


Currently only the Guice default binder and Guice Servlet binder are accessible,
Has a gzip and caching filter for sites which is always nice

Use like

public class JWebSwingSiteBinder extends GuiceSiteBinder
public class CustomerBinderJar1 extends GuiceDefaultBinder
{
    @Override
    public void onBind(GuiceSiteInjectorModule module)
    {
    }
}

Access via @Inject Injector injector or GuiceContext.Injector()



