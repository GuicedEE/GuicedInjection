GuicedEE Inject - C4 Level 3 - Component: Inject Core

```mermaid
flowchart TB
  subgraph InjectCore[GuicedEE Inject Core]
    ScanPkg[Package Scanner\nPackageContentsScanner]
    ScanFile[File Contents Scanner\nFileContentsScanner]
    SPI[Service Loader / SPI\njava.util.ServiceLoader]
    Registry[Registry\nBinders, Modules, Extensions]
    Boot[Injector Bootstrap\nGuice modules assembly]
    AOP[AOP and Interceptors\nGuice]
    Logging[Logging Bootstrap\nLog4j2 config + Log4JConfigurator SPI]
    LoggerInject[Logger Injection\n@InjectLogger + TypeListener/MembersInjector]
    Jobs[Job Service\nVirtual thread pools + polling shutdown hook]
    URLHandler[JRT URL Handler\njava.net.spi.URLStreamHandlerProvider]
    Adapters[Integration Adapters\nVert.x optional]
  end

  Classpath[(Classpath)]
  Guice[Google Guice]

  Classpath --> ScanPkg
  Classpath --> ScanFile
  ScanPkg --> SPI --> Registry
  ScanFile --> Registry
  Registry --> Boot --> Guice
  Boot --> AOP
  Boot --> Logging
  Boot --> LoggerInject
  Boot --> Jobs
  Boot --> Adapters
  App((Host App)) --> URLHandler
```

Notes
- PackageContentsScanner and FileContentsScanner are SPI discovered extension points implemented by host libraries or apps.
- Registry composes discovered Guice binders and modules before handoff to Guice injector creation.
- Logging covers root Log4j2 bootstrap, layout selection (JSON/console), and Log4JConfigurator SPI. Logger injection uses @InjectLogger with a TypeListener + MembersInjector.
- Jobs represents JobService orchestration of virtual-thread pools and polling executors with shutdown hooks.
- URL handler provides a JRT URL stream handler via JPMS service provider.
- Adapters represent optional runtime integrations such as Vert.x provided by sibling libraries.
