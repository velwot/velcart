# PureScan — Agent Engineering Rules

## Project
- Name: PureScan
- Flutter/Dart Android-first application
- Package: `com.noir.purescan`
- Independent personal project inspired by the publicly described functionality of ToxScan.
- Reference: https://play.google.com/store/apps/details?id=com.jonaswesth.toxscan

Do not copy ToxScan's source code, branding, logo, proprietary assets, exact UI, package name, or undocumented scoring algorithm.

## Technology
Use Flutter, Dart, Material 3, Riverpod, GoRouter, Dio, SQLite/Drift, and Google ML Kit where appropriate. Use Gemini only for explanation features.

## Architecture
Keep presentation, domain, data, and external services separated.
Do not put business logic, API calls, or database operations directly inside widgets.

## Agentic Workflow
For every milestone:
1. Inspect the repository and relevant docs/skills.
2. Make the smallest reasonable implementation.
3. Format code.
4. Run `flutter analyze`.
5. Run relevant tests.
6. Build Android when appropriate.
7. Fix failures before continuing.
8. Never claim a command passed unless it was actually executed.

Do not implement the entire application in one operation.

## Core Rules
- Preserve working code.
- Avoid unnecessary dependencies and rewrites.
- Never fabricate product data, API responses, scientific evidence, citations, or test results.
- Unknown ingredients are not automatically harmful.
- Gemini must not calculate or override the PureScan score.
- Never put production API keys or signing credentials in source control.
- OCR output must be shown to the user before analysis.
- The deterministic score must be independently designed and configurable.
- Call the score `PureScan Analysis Score`, not an objective toxicity measurement.
- Handle loading, success, empty, not-found, network-error, permission-denied, and unknown-error states explicitly.
- Keep scan history local by default.

## Definition of Done
A feature is complete only when implementation, error handling, relevant tests, analysis, and applicable Android build validation are complete.

## Milestones
0. Flutter foundation
1. Barcode scanner
2. Product lookup
3. Ingredient database
4. Deterministic scoring
5. Analysis UI
6. OCR
7. Gemini explanation
8. History
9. Search
10. Polish and APK
