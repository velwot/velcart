# PureScan Architecture

```text
Presentation
    ↓
ViewModel/Controller/Provider
    ↓
Use Case
    ↓
Repository
    ↓
Local / Remote Data Source
```

Suggested Flutter structure:

```text
lib/
├── app/
├── core/
├── data/
├── domain/
├── features/
└── services/
```

Keep scanner, product lookup, ingredient analysis, scoring, OCR, AI, and persistence independently testable.
