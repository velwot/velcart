# PureScan

**PureScan** is an independent personal-use Android application built with Flutter for evaluating personal care, cosmetic, and food ingredients against scientific toxicological databases.

> **Note on Reference:** Inspired functionally by independent ingredient scanners such as ToxScan for conceptual requirements. PureScan features an original identity, unique Material 3 UI design system, clean domain models, and transparent architecture.

---

## Architecture & Technology Stack

- **Framework:** Flutter 3 (Targeting Android primarily)
- **Language:** Dart
- **Design System:** Material 3 (with custom dynamic light/dark theming)
- **Architecture:** Clean Architecture + MVVM-style separation
  - **Domain:** Pure business entities, repository interfaces, and isolated use cases
  - **Data:** Local database DAOs/tables (Drift/SQLite), remote DTO models, Dio HTTP client, repository implementations
  - **Features / Presentation:** StateNotifier Riverpod controllers & modern Material 3 screens
  - **Services:** Barcode scanning, ML Kit OCR, and AI abstraction layers
- **State Management:** Riverpod (`flutter_riverpod`)
- **Navigation:** Type-safe GoRouter (`go_router`)
- **Networking:** Dio (`dio`)
- **Persistence:** Drift / SQLite local storage abstraction

---

## Directory Structure

```text
lib/
├── main.dart
│
├── app/
│   ├── app.dart
│   ├── router.dart
│   └── theme/
│       ├── app_theme.dart
│       ├── app_colors.dart
│       └── app_text_styles.dart
│
├── core/
│   ├── constants/
│   │   ├── api_constants.dart
│   │   └── app_constants.dart
│   ├── errors/
│   │   ├── app_exception.dart
│   │   └── failure.dart
│   ├── network/
│   │   └── dio_client.dart
│   ├── utils/
│   │   └── logger.dart
│   └── widgets/
│       ├── app_button.dart
│       ├── app_card.dart
│       ├── empty_state_view.dart
│       ├── error_view.dart
│       └── loading_view.dart
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   ├── dao/
│   │   └── entities/
│   ├── remote/
│   │   ├── api/
│   │   └── models/
│   └── repositories/
│
├── domain/
│   ├── entities/
│   │   ├── product.dart
│   │   ├── ingredient.dart
│   │   └── product_analysis.dart
│   ├── repositories/
│   └── usecases/
│
├── features/
│   ├── home/
│   ├── scanner/
│   ├── product/
│   ├── analysis/
│   ├── history/
│   ├── search/
│   ├── ocr/
│   ├── ai/
│   └── settings/
│
└── services/
    ├── barcode/
    ├── ocr/
    └── ai/

assets/
├── data/
│   └── ingredients.json
├── images/
└── icons/

test/
├── analysis/
├── scanner/
├── product/
└── core/

android/
ios/
pubspec.yaml
README.md
```

---

## Build & Run

### Prerequisites
- Flutter SDK 3.24+
- Android SDK with API 34+

### Verification & Testing
```bash
flutter pub get
dart analyze
flutter test
flutter build apk --debug
```

### Application ID
- Android Package / App ID: `com.noir.purescan`
