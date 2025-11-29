# RULES — GuicedEE Inject (Host Project)

Scope: Host rules for the GuicedEE Inject library consuming the Rules Repository submodule under `rules/`. Project-specific docs live outside the submodule. Forward-only and Document Modularity policies are enforced.

## Core Policies
- Follow `rules/RULES.md` sections 4–6, Document Modularity, and Forward-Only change policy.
- Documentation-first, stage-gated workflow (docs through Stage 3 before code in Stage 4).
- Use Mermaid/PlantUML sources for diagrams; keep them under docs/architecture/ with an index.
- Glossary precedence: topic glossaries override root; see GLOSSARY.md.

## Selected Stacks and References
- Language/Build: Java 25 LTS with Maven — rules/generative/language/java/java-25.rules.md and rules/generative/language/java/build-tooling.md.
- GuicedEE Core: rules/generative/backend/guicedee/README.md and rules/generative/backend/guicedee/functions/guiced-injection-rules.md.
- GuicedEE Client: rules/generative/backend/guicedee/client/README.md.
- GuicedEE Vert.x adapter (optional runtime integration): rules/generative/backend/guicedee/vertx/README.md and rules/generative/backend/guicedee/functions/guiced-vertx-rules.md.
- GuicedEE Inject topic index (rules and guides): rules/generative/backend/guicedee/inject/README.md.
- Fluent API strategy: CRTP — rules/generative/backend/fluent-api/crtp.rules.md (do not use Lombok builders here).
- Logging: rules/generative/backend/logging/README.md.
- Nullness: rules/generative/backend/jspecify/README.md.
- CI/CD: rules/generative/platform/ci-cd/README.md and rules/generative/platform/ci-cd/providers/github-actions.md.
- Architecture: rules/generative/architecture/README.md.

## Library Rules and Conventions
- JPMS module: `com.guicedee.guicedinjection`; maintain `provides/uses` wiring for scanners, providers, and services.
- Scanning and SPI:
  - PackageContentsScanner/FileContentsScanner/Path scanners remain SPI-driven; registry composes binders/modules before injector creation.
  - Keep adapters (Vert.x, etc.) optional and clearly separated from core DI.
- Logging:
  - Default Log4j2 configuration is managed in `GuiceContext`; honor environment toggles for log levels and cloud-friendly JSON console when applicable.
  - Apply logging rules for member injection (`Log4JTypeListener`) and configurator SPI.
- Nullness and types:
  - Use JSpecify annotations where available; respect CRTP generics for fluent APIs.
- Fluent APIs:
  - Use CRTP setters returning `(J)this`; avoid Lombok @Builder in this module.

## Traceability
- Pact: PACT.md
- Glossary: GLOSSARY.md (topic-first)
- Guides: GUIDES.md (how to apply the above rules)
- Implementation notes: IMPLEMENTATION.md (current modules, layouts)
- Architecture diagrams: docs/architecture/README.md (C4, sequences, ERD rationale)

## Deviations and Forward-Only Handling
- When replacing docs, update all inbound links in the same change set; no legacy anchors.
- Document any intentional deviations in MIGRATION.md if removals are risky.
