# 💉 GuicedEE Inject

[![JDK](https://img.shields.io/badge/JDK-25%2B-0A7?logo=java)](https://openjdk.org/projects/jdk/25/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

<!-- Tech icons row -->
![Guice](https://img.shields.io/badge/Guice-Core-2F4F4F)
![JSpecify](https://img.shields.io/badge/JSpecify-Nullness-4B9)
![Log4j2](https://img.shields.io/badge/Log4j2-Logging-B318)
![JPMS](https://img.shields.io/badge/JPMS-Modules-0A7)
![Vert.x (optional)](https://img.shields.io/badge/Vert.x-Adapter_Optional-4B9)

GuicedEE Inject is a lightweight, JPMS-ready integration layer for Google Guice with classpath scanning + SPI discovery. It bootstraps logging (@InjectLogger), job services (virtual-thread executors), and includes a JRT URL StreamHandler. Adapters (e.g., Vert.x) are optional and stay out of the core classpath.

## ✨ Features
- SPI- and scanner-driven discovery of Guice Modules/Binders across archives
- Logging bootstrap with `@InjectLogger` TypeListener and Log4j configurator SPI
- Job service with virtual-thread executors and graceful shutdown hooks
- JRT URL stream handler (java.net.spi.URLStreamHandlerProvider)
- Optional Vert.x adapter for reactive runtimes (kept separate)
- JPMS-ready module: `com.guicedee.guicedinjection` with dual ServiceLoader/JPMS registration

## 📦 Install (Maven)
Managed via the GuicedEE BOM/parent; add the dependency:

```xml
<dependency>
  <groupId>com.guicedee</groupId>
  <artifactId>guice-injection</artifactId>
</dependency>
```

## 🚀 Quick Start
1) Initialize the Guice context and let Inject discover modules via SPI + scanning.

```
import com.guicedee.client.IGuiceContext;

public class Main {
  public static void main(String[] args) {
    // Bootstraps scanning → SPI discovery → injector creation → logging/job startup
    IGuiceContext.instance();
  }
}
```

2) Provide a module and register it via ServiceLoader + JPMS.

```
// Module
public final class MyModule extends com.google.inject.AbstractModule
    implements com.guicedee.client.IGuiceModule<MyModule> {
  @Override protected void configure() {
    bind(Greeter.class).to(DefaultGreeter.class);
  }
}

// META-INF/services/com.guicedee.client.IGuiceModule
//   com.example.di.MyModule

// module-info.java
// module com.example.app {
//   requires com.guicedee.guicedinjection;
//   provides com.guicedee.client.IGuiceModule with com.example.di.MyModule;
//   uses com.guicedee.client.IGuiceModule;
// }
```

3) Use injected logging and services.

```
import com.guicedee.logger.annotations.InjectLogger;
import org.apache.logging.log4j.Logger;

public class DefaultGreeter implements Greeter {
  @InjectLogger
  Logger log;

  @Override public void greet(String name) {
    log.info("Hello, {}!", name);
  }
}
```

## ⚙️ Configuration
- Scanning: optionally narrow with `PackageContentsScanner` or customize with `FileContentsScanner`.
- SPI: always dual-register — `META-INF/services/<fqcn>` and `module-info.java` (provides/uses).
- Logging: provide a `Log4JConfigurator` SPI to customize appenders/patterns; defaults are sensible.
- Jobs: virtual-thread executors are provided; hook shutdown via GuicedEE lifecycle.
- URL Handler: JRT handler is installed via `URLStreamHandlerProvider` — e.g. `new URL("jrt:/java.base/module-info.class")`.

Keep secrets and environment-specific settings in your host app; GuicedEE Inject itself carries no runtime secrets.

## 🧩 JPMS & SPI
- Module name: `com.guicedee.guicedinjection`
- Add `uses` for SPI you consume; add `provides ... with ...` for implementations you ship.
- In non-JPMS environments, META-INF/services discovery still works.

## Optional adapter: Vert.x
- If your app runs a reactive stack, use the Vert.x adapter documented under:
  - `rules/generative/backend/guicedee/vertx/README.md`
  - `rules/generative/backend/guicedee/functions/guiced-vertx-rules.md`
- Keep Vert.x out of core DI modules to avoid unnecessary transitive dependencies.

## 📚 Governance & Docs
- Pact: `PACT.md`
- Rules: `RULES.md`
- Guides: `GUIDES.md`
- Implementation notes: `IMPLEMENTATION.md`
- Glossary (topic-first): `GLOSSARY.md`
- Architecture index and diagrams: `docs/architecture/README.md`
- Prompt reference for AI systems: `docs/PROMPT_REFERENCE.md`
- Implementation plan (forward-only rollout): `IMPLEMENTATION_PLAN.md`
- Inject rules index: `rules/generative/backend/guicedee/inject/README.md`

## 📝 License & Contributions
- License: Apache 2.0 (see `LICENSE`).
- Contributions: open focused issues/PRs. Follow the forward-only, documentation-as-code policy. Update ServiceLoader and JPMS entries together when adding/changing SPIs.

## 🧪 CI
- CI typically runs Maven on JDK 25 (see `.github/workflows/maven-publish.yml` in host repos).
- No `.env.example` is distributed here — host applications own environment templates and secrets.

