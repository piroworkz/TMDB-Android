---
name: android-tdd
description: Strict TDD workflow and testing discipline for the TMDB-Android repo. Use when making any code changes (including refactors) in this project to follow RED→GREEN→REFACTOR, edge-first flow, and boundary/testing rules.
---

# TMDB-Android TDD Skill

Follow repo authority order: `AGENTS.md` (guardrails) → `context/constitution.md` (process/TDD rules) → `context/PROJECT_CONTEXT.md` (ops). If there is conflict, STOP and ask.

## Use this skill to
- Apply strict TDD with RED→GREEN→REFACTOR for every change.
- Keep constructors and `init {}` side-effect free.
- Focus tests on behavior and collaborators, not structure.
- Defer architecture purity until behavior stabilizes.

## Workflow (short)
1) Start at the data edge; let tests define API/collaborators/error model.
2) RED first (may be non-compiling), then GREEN, then refactor.
3) Keep tests behavior-focused; delete tests that stop adding value.
4) Do not stop mid-iteration; end with tests GREEN.

## References
- Detailed guidance and examples: `references/TDD_FEWSHOTS.md`.
