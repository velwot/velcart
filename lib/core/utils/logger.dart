import 'package:flutter/foundation.dart';

class AppLogger {
  static void d(String message, [String? tag]) {
    if (kDebugMode) {
      debugPrint('[DEBUG]${tag != null ? ' [$tag]' : ''} $message');
    }
  }

  static void i(String message, [String? tag]) {
    if (kDebugMode) {
      debugPrint('[INFO]${tag != null ? ' [$tag]' : ''} $message');
    }
  }

  static void w(String message, [String? tag]) {
    if (kDebugMode) {
      debugPrint('[WARN]${tag != null ? ' [$tag]' : ''} $message');
    }
  }

  static void e(String message, [Object? error, StackTrace? stackTrace, String? tag]) {
    if (kDebugMode) {
      debugPrint('[ERROR]${tag != null ? ' [$tag]' : ''} $message');
      if (error != null) debugPrint('Error: $error');
      if (stackTrace != null) debugPrint('StackTrace: $stackTrace');
    }
  }
}
