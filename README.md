# GuicedEE Inject (OSS)

GuicedEE Inject is an open-source Guice integration library that discovers and composes modules and binders across JARs via SPI + classpath scanning, bootstraps logging and job services, and provides a JRT URL handler. Optional adapters (e.g., Vert.x) stay isolated so core DI remains lightweight.

## Features
- SPI- and scanner-driven discovery of Guice Modules/Binders across archives
- Logging bootstrap with @InjectLogger TypeListener and Log4JConfigurator SPI
- Job service with virtual-thread executors and graceful shutdown hooks
- JRT URL stream handler (java.net.spi.URLStreamHandlerProvider)
- Optional Vert.x adapter (reactive runtime), kept out of the core classpath
- JPMS-ready (`com.guicedee.guicedinjection`) with dual ServiceLoader/JPMS registration

## Requirements
- Java 25 LTS or newer
- Maven; dependencies managed via GuicedEE BOMs
- Google Guice (core + AOP), ClassGraph, GuicedEE Config Core (pulled via BOM)

## Get the library
Maven coordinates (managed by the GuicedEE BOM):
```xml
<dependency>
  <groupId>com.guicedee</groupId>
  <artifactId>guice-injection</artifactId>
</dependency>
```

## Quickstart
1) Implement scanners (optional, to widen search):
- `PackageContentsScanner` to whitelist package prefixes.
- `FileContentsScanner` to process specific files (register processors by filename).

2) Register ServiceLoader entries (always dual-register):
- `META-INF/services/<fqcn>` for each SPI implementation.
- `module-info.java` `provides ... with ...;` and `uses ...;` for JPMS environments.

3) Provide modules:
- Extend standard Guice modules (e.g., `AbstractModule`, `PrivateModule`) and implement `IGuiceModule<?>` (CRTP) so they are discoverable.
- Keep modules side-effect free; perform wiring in `configure`/`onBind` methods and register via SPI.

4) Bootstrap:
- Use `GuiceContext` (core bootstrap) to run scanning → SPI discovery → registry assembly → injector creation → logging/job service startup.

## Optional adapter: Vert.x
- Add adapter coordinates when running reactive services: see `rules/generative/backend/guicedee/vertx/README.md` and `rules/generative/backend/guicedee/functions/guiced-vertx-rules.md`.
- Do not make Vert.x a required dependency for core DI.

## Governance, docs, and rules
- Pact: `PACT.md`
- Project rules: `RULES.md`
- Guides: `GUIDES.md`
- Implementation notes: `IMPLEMENTATION.md`
- Glossary (topic-first): `GLOSSARY.md`
- Architecture index and diagrams: `docs/architecture/README.md`
- Prompt reference for AI systems: `docs/PROMPT_REFERENCE.md`
- Implementation plan (forward-only rollout): `IMPLEMENTATION_PLAN.md`
- GuicedEE Inject rules index: `rules/generative/backend/guicedee/inject/README.md`

## License and contributions
- License: Apache 2.0 (see `LICENSE`).
- Contributions: open an issue/PR with focused changes; follow forward-only and document-modularity policies. Keep adapters optional and update ServiceLoader/JPMS entries together.

## CI
- GitHub Actions builds/tests target JDK 25; publishing workflow lives in `.github/workflows/maven-publish.yml`. No `.env.example` is provided—host apps own their env templates.

