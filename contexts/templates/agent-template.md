# agents_template.md (TEMPLATE)

> Goal: This file is a **single source of truth** for how the repository is organized and how contributors (humans or agents)
> should work in it. It must be **repo-specific**, **actionable**, and **grounded in evidence** from the codebase.

**Project Name:** <PROJECT_NAME>  
**Primary Language(s):** <LANGUAGES>  
**Platform(s):** <PLATFORMS>  
**Runtime Targets:** <e.g., min/target API, deployment target, node version>  

---

## 0. Conventions Profile (repo-specific)
> This section captures conventions that vary widely across projects. Fill it first, then reference it elsewhere.

### 0.1 Naming & Layering Style (choose what matches the repo)
- Architecture style: <e.g., Clean Architecture, MVVM, Hexagonal, Modular monolith, etc>
- Layer names used in this repo: <e.g., domain/data/ui | core/feature | packages/apps/libs | etc>
- Module/package naming scheme: <describe what exists>

### 0.2 Suffix/Prefix Policies (describe **what this repo does**, not what it “should” do)
- Repository naming: <e.g., “FooRepository”, “FooRepositoryImpl”, “FooRepo”, etc>
- Service/data source naming: <e.g., “RemoteFooService”, “FooApi”, “FooDataSource”, etc>
- Use case / operation naming: <e.g., “GetFoo”, “FetchFooUseCase”, “LoadFoo”, etc>
- Implementation suffix policy: <e.g., “Impl used”, “Impl avoided”, “Mixed”, “Not applicable”>
- Use case suffix policy: <e.g., “UseCase suffix used”, “No UseCase suffix”, “Mixed”, “Not applicable”>

### 0.3 Comment & Documentation Policy
- Code comments: <Allowed / Discouraged / Forbidden / Mixed>
- KDoc/docstrings: <Allowed / Discouraged / Forbidden / Mixed>
- Where docs live: <e.g., /docs, /contexts, README, wiki>

### 0.4 Evidence Pointers
> For every convention above, include 2–5 pointers to evidence:
- <path/to/example1>
- <path/to/example2>

---

## How to Use This Doc
- Scan the TOC and jump to the relevant section.
- Follow the conventions in **Section 0** and **Section 3**.
- Build/test commands live in Section 8 for quick copy/paste.
- Section 6 defines the development workflow for this repo (if present).

## Table of Contents
1. Project Overview
2. Architecture & Principles
3. Naming Conventions (repo-specific)
4. Code Style & UI Framework Conventions
5. Dependencies & Stack
6. Workflow (TDD / Non-TDD / Hybrid)
7. Testing Strategy
8. Build & Tooling
9. Implementation Patterns
10. Practices & Guidance
11. Glossary
12. Quick Links

---

## 1. Project Overview

### 1.1 Repository Layout (tree)
Provide an accurate high-level tree (short but real):

```
<REPO_ROOT>/
├── <module_or_folder_1>/
├── <module_or_folder_2>/
└── ...
```

### 1.2 Module / Package Model
Describe how code is split (modules/packages/services), including real boundaries.

- Modules/packages: <what exists>
- Layering approach: <how layers map to folders/modules/packages>

### 1.3 Module/Package Registry
- Where modules/packages are declared: <e.g., settings.gradle.kts / workspace / package.json / Xcode project / Bazel>
- Include a short example snippet or list.

### 1.4 Assets / Resources Ownership (if applicable)
Define repo-specific rules like:
- Where resources/assets live
- Whether features can own their own resources
- Exceptions (if any)

---

## 2. Architecture & Principles

### 2.1 Architecture Style
- <Describe the architecture that is actually implemented>

### 2.2 Dependency Flow
Write the dependency direction used in this repo (if applicable). Example:

UI → Domain ← Data

### 2.3 Key Design Principles
Rules that constrain decisions in this repo:
- <principle 1>
- <principle 2>

---

## 3. Naming Conventions (repo-specific)

> Use the “Conventions Profile” (Section 0) as the source of truth. This section is the detailed form.

### 3.1 General
- Classes/types: <convention>
- Functions/vars: <convention>
- Constants: <convention>
- Packages/modules: <convention>
- UI components: <convention>
- State holders (VM/Presenter/Controller/etc): <convention>

### 3.2 Domain/Data/UI (or equivalent) Naming
Fill patterns *as observed in the repo*:
- Repositories: <pattern>
- Data sources/services/clients: <pattern>
- Use cases/operations/commands: <pattern>
- DTOs/models/entities: <pattern>
- Mappers/adapters: <pattern>

### 3.3 Test Doubles (if applicable)
- Mocks: <pattern or rules>
- Fakes: <pattern or rules>
- Spies: <pattern or rules>
- Fixtures/builders: <pattern or rules>

---

## 4. Code Style & UI Framework Conventions

### 4.1 Language & Idioms
- Primary language rules: <e.g., Kotlin-only, Swift-only, mixed>
- Concurrency model(s): <e.g., coroutines/Flow, async-await, Rx, threads>
- Preferred patterns: <e.g., sealed ADTs, extension funcs, protocols>

### 4.2 Formatting & Organization
- Formatting tool: <ktfmt/spotless/swiftformat/prettier/etc>
- File rules: <one public type per file? grouping?>
- Comment/KDoc policy: <from Section 0.3>
- Error handling idioms: <exceptions/results/either/etc>

### 4.3 UI Conventions (if UI exists)
- UI framework: <Compose/SwiftUI/React/etc>
- State management: <VM, Redux-like, etc>
- Previews/snapshots: <rules>
- Theming/design system: <rules>

---

## 5. Dependencies & Stack

> Source of truth for versions: <version catalog / lockfile / build system>

### 5.1 Build Tools
| Component | Version | Evidence |
|---|---:|---|
| <Build system> | <x.y.z> | <path + key> |

### 5.2 Key Libraries (group by area)
| Area | Library | Version | Evidence | Notes |
|---|---|---:|---|---|
| UI | <lib> | <x.y.z> | <path + key> | <purpose> |
| Data | <lib> | <x.y.z> | <path + key> | <purpose> |
| DI | <lib> | <x.y.z> | <path + key> | <purpose> |
| Testing | <lib> | <x.y.z> | <path + key> | <purpose> |

---

## 6. Workflow (TDD / Non-TDD / Hybrid)

### 6.1 Workflow Mode
- Mode: <Strict TDD / TDD where practical / Non-TDD / Hybrid>
- Rationale: <short>

### 6.2 Workflow Rules
Write the rules this repo enforces (if any):
- <rule 1>
- <rule 2>

### 6.3 Testing Value Rules
- What MUST be tested: <repo-specific>
- What MUST NOT be tested: <repo-specific>

---

## 7. Testing Strategy

### 7.1 Approach
- Test types used: <unit/integration/ui/e2e>
- When to prefer each

### 7.2 Locations & Conventions
- Unit tests: <path>
- Integration tests: <path>
- UI tests: <path>

### 7.3 Tooling
- Test runner/framework: <name + version + evidence>
- Mocks/fakes: <name + version + evidence>

---

## 8. Build & Tooling

### 8.1 Build commands
```bash
<build_command_1>
<build_command_2>
```

### 8.2 Test commands
```bash
<unit_test_command>
<ui_test_command>
```

### 8.3 Lint / formatting
```bash
<lint_command>
```

### 8.4 CI (optional)
- CI provider: <GitHub Actions/Circle/etc>
- Key workflows: <paths>

---

## 9. Implementation Patterns

### 9.1 Boundaries & Ownership
- Where business logic lives
- Where integration/persistence lives
- Where UI/state lives

### 9.2 Common Patterns
- <pattern 1 + short example path>
- <pattern 2 + short example path>

### 9.3 Error & Result Modeling
- Canonical approach: <exceptions / Result / Either / error codes>
- Mapping rules: <if any>

---

## 10. Practices & Guidance

### 10.1 Before making changes
- Identify impacted area
- Follow repo workflow mode (Section 6)
- Keep boundaries consistent

### 10.2 Adding dependencies
- Where to declare versions
- Approval process (if any)
- Policy for new deps

### 10.3 Creating modules/packages (if applicable)
- Naming scheme
- Registration steps
- Required templates/plugins

### 10.4 Code review checklist
- <short checklist aligned to this repo>

---

## 11. Glossary
| Term   | Meaning      |
|--------|--------------|
| <term> | <definition> |

---

## 12. Quick Links
- <internal docs>
- <build system docs>
- <platform docs>

---

**Document Version:** <VERSION>  
**Last Updated:** <YYYY-MM-DD>  
**Maintained By:** <NAME/TEAM>
