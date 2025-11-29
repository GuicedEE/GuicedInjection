# IMPLEMENTATION — GuicedEE Inject

Current code layout and runtime composition for the GuicedEE Inject library. Use this as the anchor between GUIDES and the actual source.

## Modules and Packages
- JPMS module: `com.guicedee.guicedinjection` (src/main/java/module-info.java) with `uses/provides` wiring for scanners, configurators, and providers.
- Core bootstrap: `GuiceContext` manages logging initialization, SPI discovery, ClassGraph scanning, registry composition, and Guice injector creation.
- Configuration: `GuiceConfig` exposes toggles for scanning, visibility, and classpath handling; injected via `ContextBinderGuice`.
- Bindings: `ContextBinderGuice` supplies `GuiceConfig`, `GlobalProperties`, `ScanResult`, `JobService`, and logging listeners; implements `IGuiceModule`.
- SPI Providers: `GuiceContextProvision`, `JobServiceProvision`, `GuiceDefaultModuleExclusions`, Vert.x pre-startup hook, and URL handler provider.
- Utilities/Representations: `ManagedSet`, `GlueList`, `ICopyable`, logging members injector/listener, URL handlers for `jrt:`.

## Runtime Flow (evidence)
- SPI discovery and registry composition precede injector creation — see docs/architecture/sequence-spi-discovery.md.
- Injector boot flow and adapter initialization are captured in docs/architecture/sequence-runtime-injection.md and docs/architecture/c4-component-inject.md.

## Cross-References
- Rules: RULES.md (selected stacks and constraints) and rules/generative/backend/guicedee/inject/README.md (topic index)
- Guides: GUIDES.md (how to apply rules and adapters)
- Glossary: GLOSSARY.md (topic-first terminology)
- Architecture index: docs/architecture/README.md
- Pact: PACT.md (collaboration and stage-gate policy)
