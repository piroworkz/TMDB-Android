---
description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/[feature-name]/`  
**Prerequisites**: [feature]_plan.md (required), [feature]_specs.md (required for user stories)

## Testing Policy (IMPORTANT)

**Tests**: Tests are **REQUIRED by default** for every user story.

**TDD**: **OPTIONAL**.
- If a User Story is marked as **TDD**, tasks MUST follow **Red → Green → Refactor** and explicitly include all three phases in the task list.
- If a User Story is **not** marked as TDD, tests are still REQUIRED, but **Red/Green/Refactor is NOT required**.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

---

## Task format (MANDATORY)

Each task is a small, actionable unit of work.

### Format (block)

- [ ] <ID> <P?> <Story?> <Title>
  Description: <what/why in 2–4 lines; keep indentation>
  <optional extra description lines>
  Paths:
  - <path/to/file1>
  - <path/to/file2>
  Depends on: <optional, e.g., T006>

Where:
- <ID> is required (e.g., T001).
- <P?> is optional. Use "[P]" only when tasks can truly run in parallel (different files, no ordering dependency).
- <Story?> is optional. Use "[US1]", "[US2]", etc. Setup/Foundational tasks typically omit it.
- <Title> must be human-readable and MUST NOT include file paths, packages, or long technical details.
- File paths MUST appear ONLY under "Paths:".
- "Description" may span 2–4 lines as long as indentation is preserved.
- "Depends on" is optional but recommended when ordering matters.

### Example

- [ ] T004 [P] Setup database schema and migrations framework
  Description: Establish the baseline persistence structure and migration strategy required by all user stories.
  Include a simple initial migration and guidelines for future schema changes.
  Paths:
    - feature/<feature>/src/main/kotlin/.../Database.kt
    - feature/<feature>/src/main/kotlin/.../dao/*.kt
      Depends on: T002

---

## Path Conventions

- **Single project**: `src/`, `tests/` at repository root
- **Web app**: `backend/src/`, `frontend/src/`
- **Mobile**: `api/src/`, `ios/src/` or `android/src/`
- Paths shown below assume single project - adjust based on [feature]_plan.md structure

<!--
============================================================================
IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.

The tasks prompt MUST replace these with actual tasks based on:
- User stories from [feature]_specs.md (with their priorities P1, P2, P3...)
- Feature requirements from [feature]_plan.md

Tasks MUST be organized by user story so each story can be:
- Implemented independently
- Tested independently
- Delivered as an MVP increment

DO NOT keep these sample tasks in the generated [feature]_tasks.md file.
============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T002 Initialize [language] project with [framework] dependencies
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T003 [P] Configure linting and formatting tools
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Setup database schema and migrations framework
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T005 [P] Implement authentication/authorization framework
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T006 [P] Setup API routing and middleware structure
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T007 Create base models/entities that all stories depend on
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T008 Configure error handling and logging infrastructure
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T009 Setup environment configuration management
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Testing Mode for User Story 1 (MANDATORY)

Choose exactly one:

- [ ] **TDD** (tests REQUIRED + must include Red/Green/Refactor tasks)
- [ ] **Non-TDD** (tests REQUIRED + no Red/Green/Refactor requirement)

### Tests for User Story 1 (REQUIRED) ⚠️

> If **TDD** is selected: tests MUST be explicitly split into RED/GREEN/REFACTOR tasks.  
> If **Non-TDD** is selected: tests must still be included, but do NOT require explicit R/G/R phase wording.

- [ ] T010 [P] [US1] Contract test for [endpoint] in tests/contract/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T011 [P] [US1] Integration test for [user journey] in tests/integration/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Implementation for User Story 1

- [ ] T012 [P] [US1] Create [Entity1] model in src/models/[entity1].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T013 [P] [US1] Create [Entity2] model in src/models/[entity2].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T014 [US1] Implement [Service] in src/services/[service].py (depends on T012, T013)
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T015 [US1] Implement [endpoint/feature] in src/[location]/[file].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T016 [US1] Add validation and error handling
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T017 [US1] Add logging for user story 1 operations
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Refactor for User Story 1 (MANDATORY)

- [ ] T018 [US1] Refactor for readability/performance while keeping tests green
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Testing Mode for User Story 2 (MANDATORY)

Choose exactly one:

- [ ] **TDD** (tests REQUIRED + must include Red/Green/Refactor tasks)
- [ ] **Non-TDD** (tests REQUIRED + no Red/Green/Refactor requirement)

### Tests for User Story 2 (REQUIRED) ⚠️

> If **TDD** is selected: tests MUST be explicitly split into RED/GREEN/REFACTOR tasks.  
> If **Non-TDD** is selected: tests must still be included, but do NOT require explicit R/G/R phase wording.

- [ ] T019 [P] [US2] Contract test for [endpoint] in tests/contract/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T020 [P] [US2] Integration test for [user journey] in tests/integration/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Implementation for User Story 2

- [ ] T021 [P] [US2] Create [Entity] model in src/models/[entity].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T022 [US2] Implement [Service] in src/services/[service].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T023 [US2] Implement [endpoint/feature] in src/[location]/[file].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T024 [US2] Integrate with User Story 1 components (if needed)
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Refactor for User Story 2 (MANDATORY)

- [ ] T025 [US2] Refactor for readability/performance while keeping tests green
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Testing Mode for User Story 3 (MANDATORY)

Choose exactly one:

- [ ] **TDD** (tests REQUIRED + must include Red/Green/Refactor tasks)
- [ ] **Non-TDD** (tests REQUIRED + no Red/Green/Refactor requirement)

### Tests for User Story 3 (REQUIRED) ⚠️

> If **TDD** is selected: tests MUST be explicitly split into RED/GREEN/REFACTOR tasks.  
> If **Non-TDD** is selected: tests must still be included, but do NOT require explicit R/G/R phase wording.

- [ ] T026 [P] [US3] Contract test for [endpoint] in tests/contract/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T027 [P] [US3] Integration test for [user journey] in tests/integration/test_[name].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Implementation for User Story 3

- [ ] T028 [P] [US3] Create [Entity] model in src/models/[entity].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T029 [US3] Implement [Service] in src/services/[service].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

- [ ] T030 [US3] Implement [endpoint/feature] in src/[location]/[file].py
  Description: <Explain what needs to be done and why.>
  <Add 1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

### Refactor for User Story 3 (MANDATORY)

- [ ] T031 [US3] Refactor for readability/performance while keeping tests green
  Description: <Explain what needs to be done and why.>
  <Add  1–3 more lines if needed.>
  Paths:
    - <path/to/file/or/dir>
      Depends on: <optional T###>

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (as needed) in tests/unit/
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
    - User stories can then proceed in parallel (if staffed)
    - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests are REQUIRED for every user story.
- If **TDD** selected:
    - Tests MUST be written first and FAIL before implementation (RED)
    - Implement the minimum to pass (GREEN)
    - Improve code while keeping tests green (REFACTOR)
- If **Non-TDD** selected:
    - Tests must still be included, but Red/Green/Refactor wording is not required.
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Contract test for [endpoint] in tests/contract/test_[name].py"
Task: "Integration test for [user journey] in tests/integration/test_[name].py"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] model in src/models/[entity1].py"
Task: "Create [Entity2] model in src/models/[entity2].py"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
    - Developer A: User Story 1
    - Developer B: User Story 2
    - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing if TDD is selected
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
