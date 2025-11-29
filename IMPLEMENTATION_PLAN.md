# Implementation Plan — GuicedEE Inject Rules (Stage 3)

Purpose
- Define the plan to implement and validate the GuicedEE Inject rules/docs (forward-only, docs-first). No code changes until Stage 4 approval.

Planned file/module updates (rules/docs)
- rules/generative/backend/guicedee/inject/
  - README.md (index)
  - lifecycle.rules.md
  - configuration.rules.md
  - extension-points.rules.md
  - adapters-vertx.rules.md (optional adapter guidance)
  - testing.rules.md
  - release-notes-outline.md
- Root/navigation alignment (as needed in Stage 4):
  - README.md and RULES.md cross-links
  - GUIDES.md additions (how to apply rules)
  - GLOSSARY.md updates for new terms/SPIs
  - PACT.md linkbacks if required

Validation and lint strategy
- Markdown lint/check: ensure headings, links, and anchors resolve; keep fenced code blocks properly tagged (mermaid/java).
- Link validation: cross-check relative paths to diagrams (docs/architecture/*) and referenced rules (rules/generative/...).
- Consistency checks: Fluent API (CRTP) statements align with rules/generative/backend/fluent-api/crtp.rules.md; Java 25 LTS references match docs/PROMPT_REFERENCE.md.
- Forward-only: avoid deleting legacy anchors without updating inbound references in the same change set.

CI/docs publishing plan
- No CI changes required for docs-only updates. Ensure any added docs do not require build steps.
- If future publishing is needed, reuse existing GitHub Actions (ci.yml/maven-publish.yml) without modifications.

Rollout plan
- Stage 4: implement doc changes per above, update top-level navigation (README/RULES/GUIDES/GLOSSARY) to link the new rules set, and refresh release-notes outline with actual entries if changes are breaking.
- After Stage 4: validate links, run markdown/link checks locally if available, and summarize changes for user review.

Risks and mitigations
- Broken links across rules/docs: mitigate with explicit path checks.
- Scope creep into code changes during docs stages: prevent by limiting edits to markdown per gate policy.
- Adapter coupling: keep Vert.x content optional and isolated; do not add Hibernate Reactive references.

Next steps
- Await Stage 4 approval (or proceed if approvals are waived) to implement navigation updates and any doc refinements.
