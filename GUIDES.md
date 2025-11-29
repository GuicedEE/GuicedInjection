# GUIDES — How to Apply the Rules

These guides map the selected rules to practical steps for the GuicedEE Inject library. Use them alongside RULES.md and GLOSSARY.md; align terminology with architecture diagrams under docs/architecture/.

## Core Injection and Scanning
- GuicedEE Inject rules index — rules/generative/backend/guicedee/inject/README.md
- Lifecycle and boot — rules/generative/backend/guicedee/inject/lifecycle.rules.md
- Configuration and scanning — rules/generative/backend/guicedee/inject/configuration.rules.md
- Extension points (SPI) — rules/generative/backend/guicedee/inject/extension-points.rules.md
- Injector assembly and runtime flow — docs/architecture/sequence-runtime-injection.md and docs/architecture/c4-component-inject.md
- SPI discovery (scanners + ServiceLoader) — docs/architecture/sequence-spi-discovery.md
- Fluent API (CRTP setters) — rules/generative/backend/fluent-api/crtp.rules.md

## Adapters and Runtime Integration
- Vert.x adapter (optional) — rules/generative/backend/guicedee/inject/adapters-vertx.rules.md plus rules/generative/backend/guicedee/functions/guiced-vertx-rules.md and rules/generative/backend/guicedee/vertx/README.md
- Client-facing guidance — rules/generative/backend/guicedee/client/README.md

## Logging and Diagnostics
- Logging patterns and configurator SPI — rules/generative/backend/logging/README.md; see Log4JTypeListener and Log4JConfigurator SPI wiring in code.
- Nullness alignment — rules/generative/backend/jspecify/README.md

## Build, CI, and Environment
- Java 25 + Maven conventions — rules/generative/language/java/java-25.rules.md and build-tooling references.
- CI/CD alignment — rules/generative/platform/ci-cd/providers/github-actions.md; document required secrets in CI docs/workflows.
- Environment variables: No `.env.example` is maintained for this library; host projects should handle their own env templates per rules/generative/platform/secrets-config/env-variables.md if needed.

## API Surface Sketch (Library)
- SPI contracts: PackageContentsScanner, FileContentsScanner, Path scanners, Guice Module/ Binder providers, Log4JConfigurator — see `src/main/java/module-info.java` and rules/generative/backend/guicedee/functions/guiced-injection-rules.md.
- Bootstrap: `GuiceContext` orchestrates scanning, SPI loading, registry composition, and injector creation; optional Vert.x pre-startup hook.
- Bindings: `ContextBinderGuice` installs core bindings (config, scan result, job service, logging listener).
- Nullness/typing: CRTP-based fluent setters in config classes; JSpecify annotations where available.

## Design Validation & Migration Notes
- Forward-only doc model: keep rules/guides modular; replace monolithic docs with links to `rules/` topics and architecture diagrams.
- Optional runtime adapters (Vert.x) must remain opt-in and clearly separated from core DI.
- Migration from legacy docs: use docs/architecture/* diagrams and this GUIDES.md as canonical references; do not reintroduce guice-servlet/guice-persist claims.

## Test Strategy & Acceptance Criteria (Outline)
- Rules: rules/generative/backend/guicedee/inject/testing.rules.md
- Unit/IT focus: verify SPI loading paths, binder/module composition, and Log4JConfigurator SPI integration without touching external services.
- Deterministic scanning: tests should control ClassGraph inputs (package whitelists) and assert registry outcomes.
- Logging: assert default log level selection via env/system properties and cloud JSON layout toggle behavior.
- Acceptance: Injector builds successfully with discovered binders/modules; optional Vert.x adapter remains optional; CRTP fluent setters retain type safety; JPMS services resolve as declared in module-info.

## Cross-References
- Architecture index — docs/architecture/README.md
- Implementation notes — IMPLEMENTATION.md (module layout and bindings)
