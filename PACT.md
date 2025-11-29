# GuicedEE Inject — Human × AI Pact (v2)

Project alignment for the GuicedEE Inject library, based on `rules/creative/pact.md`. This pact governs how humans and AI collaborate on this repository and closes loops across PACT ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ diagrams.

## Purpose
- Maintain shared language, tone, and continuity for forward-only, documentation-first work.
- Keep all artifacts in Markdown with diagram sources in Mermaid.
- Respect host project boundaries: project docs stay outside the `rules` submodule.

## Principles
- Continuity: carry context across threads; pin `rules/RULES.md` sections 4–6 and the Document Modularity Policy.
- Finesse: concise, precise technical writing; avoid invented architecture.
- Closing loops: every change links to its parent rules and forward guides.
- Transparency: surface assumptions and risks explicitly; prefer CRTP fluent chaining over Lombok builders per fluent API strategy.

## Ways of Working
- Stage-gated, documentation-first workflow (approvals are optional per prompt):
  - Stage 1: Architecture & Foundations (docs and diagrams only).
  - Stage 2: Guides & Design Validation (docs only).
  - Stage 3: Implementation Plan (no code yet).
  - Stage 4: Implementation & Scaffolding (code allowed after docs).
- If no reply at a gate, proceed with optional approval logged; never place project docs inside `rules/`.
- Follow Glossary Precedence Policy (topic glossaries override root scope).

## Selected Stacks and Constraints
- Language/Build: Java 25 LTS, Maven; JPMS module `com.guicedee.guicedinjection`.
- Frameworks: GuicedEE Inject core with Client + Vert.x adapters; Logging; JSpecify; Vert.x 5 optional runtime integration.
- CI: GitHub Actions (to be aligned in CI docs/workflows).
- Fluent API strategy: CRTP chaining for fluent setters; do not introduce Lombok builders in this library.

## Traceability Map
- Rules base: `rules/RULES.md` (sections 4–6, Document Modularity, Forward-Only).
- Project RULES: `RULES.md`
- Guides: `GUIDES.md`
- Implementation notes: `IMPLEMENTATION.md`
- Glossary: `GLOSSARY.md` (topic-first)
- Architecture index: `docs/architecture/README.md`
- Prompt reference: `docs/PROMPT_REFERENCE.md`

## Commitments
- Docs-as-code only for stages 1–3; code changes only in Stage 4.
- Keep architecture diagrams current (C4, sequence, ERD rationale).
- Update references in the same change set when files are renamed/replaced.
