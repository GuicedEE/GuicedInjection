Architecture Foundations — Stage 1 (GuicedEE Inject)

Scope and goals
- Library scope: Provide Guice-based dependency injection and SPI-driven module/binder discovery for the GuicedEE ecosystem, including URL handler extension, logging bootstrap, and job service orchestration. Forward-only change policy applies; adapters (e.g., Vert.x) remain optional.
- Architecture stance: Specification-Driven Design with Documentation-as-Code. All diagrams live under docs/architecture/ as Mermaid sources.

Architecture overview (links)
- C4 L1 context: docs/architecture/c4-context.md
- C4 L2 container map: docs/architecture/c4-container.md
- C4 L3 (Inject core components): docs/architecture/c4-component-inject.md
- Core flows: docs/architecture/sequence-runtime-injection.md, docs/architecture/sequence-spi-discovery.md, docs/architecture/sequence-logger-injection.md, docs/architecture/sequence-job-service.md
- ERD/rationale (config surface and registries): docs/architecture/erd-config.md

Trust boundaries and threat model summary
- Boundaries: ServiceLoader inputs (SPI JARs), classpath scanning (packages/files), URL handler (jrt: scheme), environment/system properties for logging/executor sizing, optional Vert.x adapter surfaces.
- Threats and mitigations:
  - Untrusted SPI/providers: validate classpath sources; prefer allowlists for scanners/binders; document JPMS requires/provides wiring.
  - Classpath scanning abuse: restrict scan roots to configured package prefixes; reject non-whitelisted jars when configured via IGuiceScanJarExclusions/Includes.
  - URL handler misuse: handler limited to JRT module paths; avoid fetching remote/network content; rely on JPMS service registration.
  - Logging injection: InjectLogger TypeListener only targets classes in configured modules; Log4JConfigurator SPI should sanitize property-driven overrides.
  - Job service overload: cap virtual-thread pools and polling intervals via configuration; ensure graceful shutdown hooks.

Dependency and integration map
```mermaid
flowchart LR
  HostApp[Host Application] -->|uses| GuicedEE[GuicedEE Inject]
  GuicedEE -->|builds on| Guice[Google Guice]
  GuicedEE -->|configures| Log4j[Log4j2]
  GuicedEE -->|discovers| SPI[java.util.ServiceLoader providers]
  GuicedEE -->|optional| Vertx[Vert.x 5 Adapter]
  GuicedEE -->|provides| URLHandler[JRT URLStreamHandlerProvider]
  GuicedEE -->|exposes| Jobs[Job Service (virtual threads)]
```

Interaction/data flow notes
- Runtime injector assembly: Scanner → SPI discovery → registry → Guice injector (see docs/architecture/sequence-runtime-injection.md).
- SPI loading path: ServiceLoader providers (modules/binders/scanners/configurators) contribute to registry (see docs/architecture/sequence-spi-discovery.md).
- Logging bootstrap and @InjectLogger wiring: Log4JConfigurator SPI prepares appenders/layouts; TypeListener injects loggers post-construction (see docs/architecture/sequence-logger-injection.md).
- Job service lifecycle: executor provisioning, task submission, and shutdown hooks for virtual-thread pools (see docs/architecture/sequence-job-service.md).

Glossary composition plan (topic-first)
- Authoritative topic glossary: GLOSSARY.md (root, topic-first) with links to Rules Repository glossaries (GuicedEE core/client/vertx, JSpecify, Fluent API, Java 25).
- Host projects should link to this glossary and copy only enforced prompt-alignment mappings; avoid duplicating definitions already covered by topic glossaries.
- Update glossary entries when new SPI types or configuration surfaces are added; reference architecture diagrams for context anchors.
