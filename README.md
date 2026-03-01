# GuicedEE Inject

[![Build](https://github.com/GuicedEE/GuicedInjection/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/GuicedEE/GuicedInjection/actions/workflows/maven-publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.guicedee/guice-injection)](https://central.sonatype.com/artifact/com.guicedee/guice-injection)
[![Maven Snapshot](https://img.shields.io/nexus/s/com.guicedee/guice-injection?server=https%3A%2F%2Foss.sonatype.org&label=Maven%20Snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/guicedee/guice-injection/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)

![Java 25+](https://img.shields.io/badge/Java-25%2B-green)
![Guice 7](https://img.shields.io/badge/Guice-7%2B-green)
![Vert.X 5](https://img.shields.io/badge/Vert.x-5%2B-green)
![Maven 4](https://img.shields.io/badge/Maven-4%2B-green)

The **runtime engine** for [GuicedEE](https://github.com/GuicedEE).
This is the library you add to your application — it wires together the [Client SPI](https://github.com/GuicedEE/Client), performs classpath scanning, creates the Guice injector, and manages the full startup/shutdown lifecycle.

Built on [Google Guice](https://github.com/google/guice) · JPMS module `com.guicedee.guicedinjection` · Java 25+

## 📦 Installation

```xml
<dependency>
  <groupId>com.guicedee</groupId>
  <artifactId>inject</artifactId>
</dependency>
```

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
implementation("com.guicedee:guice-injection:2.0.0-SNAPSHOT")
```
</details>

## 🚀 Quick Start

```java
// Register your module for classpath scanning
IGuiceContext.registerModuleForScanning.add("my.app");

// Bootstrap — scanning, SPI discovery, injector creation, lifecycle hooks
IGuiceContext.instance();

// Grab any managed instance
MyService svc = IGuiceContext.get(MyService.class);
```

Provide a Guice module via JPMS + ServiceLoader:

```java
public class AppModule extends AbstractModule
        implements IGuiceModule<AppModule> {

    @Override
    protected void configure() {
        bind(Greeter.class).to(DefaultGreeter.class);
    }
}
```

```java
module my.app {
    requires com.guicedee.guicedinjection;

    provides com.guicedee.client.services.lifecycle.IGuiceModule
        with my.app.AppModule;
}
```

## 📝 Logging — `@InjectLogger`

Inject a named Log4j2 logger into any Guice-managed class — no boilerplate, no static fields:

```java
public class OrderService {

    @InjectLogger("orders")
    Logger log;

    public void place(Order order) {
        log.info("Placed order {}", order.id());
    }
}
```

| Attribute | Default | Purpose |
|---|---|---|
| `value` | declaring class name | Logger name |
| `level` | *(none)* | Optional level hint |
| `rollingFileName` | *(none)* | File name hint for rolling appenders |
| `fileName` | *(none)* | File name hint for file appenders |

The `Log4JTypeListener` and `Log4JMembersInjector` handle the wiring — they're registered automatically by `ContextBinderGuice`.

### Programmatic logging with `LogUtils`

For setup outside of injection (e.g. `main()` or pre-startup hooks):

```java
// ANSI-highlighted console (local dev)
LogUtils.addHighlightedConsoleLogger();

// Plain console at a specific level
LogUtils.addConsoleLogger(Level.INFO);

// Rolling file logger (100 MB / daily rollover)
LogUtils.addFileRollingLogger("my-app", "logs");

// Isolated logger with its own file
Logger auditLog = LogUtils.getSpecificRollingLogger(
    "audit", "logs/audit", null, false
);
```

> When the `CLOUD` environment variable is set, all layouts automatically switch to compact JSON for log aggregator ingestion.

## ⚙️ Job Service

`JobService` manages named virtual-thread executor pools with graceful shutdown:

```java
JobService jobs = JobService.INSTANCE;

// Register and submit work
jobs.registerJob("import", 100);
jobs.addJob("import", () -> processFile(file));

// Scheduled polling
jobs.registerPollingJob("heartbeat", () -> ping(), 0, 30, TimeUnit.SECONDS);
```

All pools are shut down automatically via `IGuicePreDestroy` when the context tears down.

## 🔍 Classpath Scanner Configuration

`GuiceContext` uses [ClassGraph](https://github.com/classgraph/classgraph) under the hood.
Control the scan scope with SPI interfaces — implement and register via `ServiceLoader` / JPMS `provides`:

### Module & JAR filtering

| SPI Interface | Method | Purpose |
|---|---|---|
| `IGuiceScanModuleInclusions` | `includeModules()` | Modules to **include** |
| `IGuiceScanModuleExclusions` | `excludeModules()` | Modules to **exclude** |
| `IGuiceScanJarInclusions` | `includeJars()` | JAR filenames to **include** |
| `IGuiceScanJarExclusions` | `excludeJars()` | JAR filenames to **exclude** |

```java
public class MyExclusions
        implements IGuiceScanModuleExclusions<MyExclusions> {

    @Override
    public Set<String> excludeModules() {
        return Set.of("java.sql", "jdk.crypto.ec");
    }
}
```

### Package & path filtering

| SPI Interface | Method | Purpose |
|---|---|---|
| `IPackageContentsScanner` | `searchFor()` | Packages to **include** |
| `IPackageRejectListScanner` | `exclude()` | Packages to **exclude** |
| `IPathContentsScanner` | `searchFor()` | Resource paths to **include** |
| `IPathContentsRejectListScanner` | `searchFor()` | Resource paths to **exclude** |

### File content scanners

These fire during the ClassGraph scan and let you process matched resources inline:

| SPI Interface | Method | Match by |
|---|---|---|
| `IFileContentsScanner` | `onMatch()` | Exact file name |
| `IFileContentsPatternScanner` | `onMatch()` | Regex pattern |

```java
public class ChangelogScanner implements IFileContentsScanner {

    @Override
    public Map<String, ResourceList.ByteArrayConsumer> onMatch() {
        return Map.of("changelog.xml", (resource, bytes) -> {
            // process matched resource
        });
    }
}
```

### `GuiceConfig` options

| Setter | Default | Effect |
|---|---|---|
| `setFieldScanning(true)` | `false` | Enable field-level scanning |
| `setAnnotationScanning(true)` | `false` | Enable annotation scanning |
| `setMethodInfo(true)` | `false` | Include method metadata |
| `setIgnoreFieldVisibility(true)` | `false` | Scan non-public fields |
| `setIncludePackages(true)` | `false` | Whitelist-only package scanning |
| `setVerbose(true)` | `false` | Verbose ClassGraph output |

Configure via an `IGuiceConfigurator` SPI implementation.

## 🔄 Lifecycle

```
IGuicePreStartup  →  ClassGraph scan  →  Injector created  →  IGuicePostStartup
                                                                        ↓
                                                               IGuicePreDestroy (shutdown)
```

| Hook | Purpose |
|---|---|
| `IGuicePreStartup` | Runs before scanning and injector creation |
| `IGuicePostStartup` | Runs after the injector is ready |
| `IGuicePreDestroy` | Cleanup on shutdown (e.g. `JobService`) |
| `IGuiceConfigurator` | Configures `GuiceConfig` before scanning |
| `Log4JConfigurator` | Customizes Log4j2 appenders/patterns at startup |

## 🗺️ Module Graph

```
com.guicedee.guicedinjection
 ├── com.guicedee.client          (SPI contracts)
 ├── com.guicedee.vertx           (Vert.x integration)
 ├── io.smallrye.config.core      (MicroProfile Config)
 └── org.apache.commons.lang3
```

## 🧩 JPMS

Module name: **`com.guicedee.guicedinjection`**

The module declares `uses` for every scanner and lifecycle SPI, and provides default implementations for module/jar exclusions, the Guice context provider, and JRT URL handling.

In non-JPMS environments, `META-INF/services` discovery still works.

## 🤝 Contributing

Issues and pull requests are welcome.

## 📄 License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
