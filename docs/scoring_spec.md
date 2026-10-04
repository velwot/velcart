# PureScan Scoring Specification

The score is an independent informational score.

Start at 100 and apply configurable penalties based on the application's own ingredient database.

Initial example configuration:

- NONE: 0
- LOW: 2
- MODERATE: 6
- HIGH: 12

Clamp the final result to 0–100.

These values are a starting implementation only and are not a medical or scientific measurement. Keep them centralized and easy to change.

Do not copy or reverse-engineer ToxScan's undocumented scoring algorithm.
