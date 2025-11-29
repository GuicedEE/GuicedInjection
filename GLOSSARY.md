# GLOSSARY — GuicedEE Inject (Topic-First)

## Glossary Precedence Policy
- Topic glossaries are authoritative for their scope and override root definitions.
- Root glossary aggregates links and only adds project-specific terms not covered by topic files.
- When prompting, reference the topic link first; avoid duplicating definitions already covered by topic glossaries.

## Topic Glossaries (Selected Stacks)
- GuicedEE (core): rules/generative/backend/guicedee/GLOSSARY.md
- GuicedEE Client: rules/generative/backend/guicedee/client/GLOSSARY.md
- GuicedEE Vert.x: rules/generative/backend/guicedee/vertx/GLOSSARY.md
- JSpecify: rules/generative/backend/jspecify/GLOSSARY.md
- Fluent API: rules/generative/backend/fluent-api/GLOSSARY.md
- Java (LTS): rules/generative/language/java/GLOSSARY.md

## Project-Specific Terms
- GuiceContext: Central bootstrapper that assembles scanners, SPI providers, and Guice Modules/Binders before creating the Injector; see docs/architecture/c4-component-inject.md.
- PackageContentsScanner / FileContentsScanner: SPI extension points that contribute packages and file processors into the registry prior to injector creation; see docs/architecture/sequence-runtime-injection.md.
- Registry: The composition step that aggregates binder/module contributions discovered via SPI and scanning before Injector creation.
- CRTP fluent chaining: Fluent setters return `(J)this` to support type-safe chaining; Lombok builders are not used in this library.
