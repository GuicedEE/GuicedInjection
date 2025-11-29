GuicedEE Inject — Prompt Reference

Overview
- This document pins the selected stacks, language/toolchain, and links to architecture diagrams for AI assistants acting on this repository. It is the canonical reference for prompts and documentation-as-code.

Selected stacks and policies
- Architecture: Specification-Driven Design (SDD), Documentation-as-Code (Mermaid), Forward-Only Change Policy
- Language (JVM): Java 25 LTS (standardized for this library’s docs and guides)
- Build: Maven (artifact coordinates documented via rules; plugin wiring lives in build-tooling guides)
- Reactive/Adapters: Vert.x 5 (optional adapter via guiced-vertx)
- Core Library Area: GuicedEE Inject (DI with Guice, SPI discovery, classpath scanning)
- Structural: Logging, JSpecify (nullness)
- CI/CD: GitHub Actions

Glossary composition (topic-first)
- Authoritative topic glossary for this library lives under docs/ (to be created/maintained alongside rules).
- Host projects should link to this glossary and only copy enforced prompt-alignment mappings when necessary.

Diagrams index (Docs-as-Code)
- C4 Level 1 (Context): docs/architecture/c4-context.md
- C4 Level 2 (Container): docs/architecture/c4-container.md
- C4 Level 3 (Component — Inject Core): docs/architecture/c4-component-inject.md
- Sequence — Runtime Injection Lifecycle: docs/architecture/sequence-runtime-injection.md
- Sequence — SPI Discovery & Module Loading: docs/architecture/sequence-spi-discovery.md
- Sequence — Logger Injection Flow: docs/architecture/sequence-logger-injection.md
- Sequence — Job Service Lifecycle: docs/architecture/sequence-job-service.md
- ERD/Schema (rationale): docs/architecture/erd-config.md

Notes for AI systems
- Always load this file and the diagrams before proposing changes.
- Respect the forward-only policy and document modularity. Do not reintroduce guice-servlet, guice-persist, or transaction-manager claims in this library’s docs.
- Optional runtime integrations (e.g., Vert.x) are provided by sibling adapters; keep them clearly optional in diagrams and docs.
