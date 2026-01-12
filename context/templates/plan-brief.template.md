# planBrief.template.md

> **Purpose**  
> This document captures **existing technical facts and constraints** of the repository that are relevant for planning a new feature.  
> It is **optional**.  
> If present, it is **authoritative for repository facts**.

---

## 1. Feature Context

```md
Feature name:
Primary TARGET_SCOPE module:
```

Describe where the feature primarily lives and which module owns it.

---

## 2. Existing Modules & Ownership

```md
- Domain module(s) involved:
- Framework module(s) involved:
- UI module(s) involved:
```

Optional notes:
- existing cross-module dependencies
- known coupling constraints

---

## 3. Existing Persistence / Data Layer (if any)

```md
Database(s):
- Name:
- Location (module + path):

Entities / Tables:
- Entity name:
- File path:

DAOs / Repositories:
- Name:
- File path:
```

Constraints:
- Reuse required? (yes/no)
- New tables allowed? (yes/no)
- Migration strategy (if applicable):

---

## 4. Existing Models / Contracts

```md
Domain models involved:
- Name + path

Use case interfaces involved:
- Name + path
```

Notes:
- Are these contracts stable?
- Are changes expected or forbidden?

---

## 5. Existing UI Entry Points

```md
Screens / ViewModels already present:
- Name + path

Navigation:
- Existing routes affected:
```

---

## 6. Resource & Asset Constraints

```md
UI resources policy:
- All resources live in: feature/core/core_ui/src/main/res/
```

(Do not list resources here — only constraints.)

---

## 7. Technical Constraints & Non-Goals

```md
Must NOT:
- Create new databases
- Change existing architecture
- Introduce new libraries
```

(Add only hard constraints.)

---

## 8. Known Risks / Gotchas (Optional)

```md
- Legacy code involved?
- Migration complexity?
- Tight coupling areas?
```

---

## 9. Authority Statement (DO NOT REMOVE)

```md
If this PlanBrief is present:
- It is authoritative for repository facts.
- The Plan MUST NOT contradict it.
- If conflicts or ambiguities are detected, the agent MUST ask before proceeding.
```
