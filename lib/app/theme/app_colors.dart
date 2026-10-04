import 'package:flutter/material.dart';

class AppColors {
  // Brand Primary (Clean Emerald)
  static const Color primary = Color(0xFF0F766E); // Deep Teal/Emerald
  static const Color onPrimary = Colors.white;
  static const Color primaryContainer = Color(0xFFCCFBF1);
  static const Color onPrimaryContainer = Color(0xFF115E59);

  // Secondary (Sage / Earth)
  static const Color secondary = Color(0xFF134E4A);
  static const Color onSecondary = Colors.white;
  static const Color secondaryContainer = Color(0xFFE6F4F1);
  static const Color onSecondaryContainer = Color(0xFF042F2E);

  // Tertiary (Clean Indigo / Deep Blue Accent)
  static const Color tertiary = Color(0xFF3B82F6);
  static const Color onTertiary = Colors.white;
  static const Color tertiaryContainer = Color(0xFFDBEAFE);
  static const Color onTertiaryContainer = Color(0xFF1E40AF);

  // Background & Surface - Light
  static const Color backgroundLight = Color(0xFFF8FAFC);
  static const Color surfaceLight = Colors.white;
  static const Color surfaceVariantLight = Color(0xFFF1F5F9);
  static const Color onSurfaceLight = Color(0xFF0F172A);
  static const Color onSurfaceVariantLight = Color(0xFF475569);
  static const Color outlineLight = Color(0xFFCBD5E1);

  // Background & Surface - Dark
  static const Color backgroundDark = Color(0xFF0F172A);
  static const Color surfaceDark = Color(0xFF1E293B);
  static const Color surfaceVariantDark = Color(0xFF334155);
  static const Color onSurfaceDark = Color(0xFFF8FAFC);
  static const Color onSurfaceVariantDark = Color(0xFF94A3B8);
  static const Color outlineDark = Color(0xFF475569);

  // Concern Levels for Ingredients
  static const Color concernLow = Color(0xFF10B981); // Emerald Green
  static const Color concernLowContainer = Color(0xFFD1FAE5);
  static const Color concernModerate = Color(0xFFF59E0B); // Amber
  static const Color concernModerateContainer = Color(0xFFFEF3C7);
  static const Color concernHigh = Color(0xFFEF4444); // Crimson / Coral
  static const Color concernHighContainer = Color(0xFFFEE2E2);
  static const Color concernUnknown = Color(0xFF64748B); // Slate Gray
  static const Color concernUnknownContainer = Color(0xFFF1F5F9);

  // Helpers
  static Color getConcernColor(String? concernLevel) {
    switch (concernLevel?.toLowerCase().trim()) {
      case 'low':
      case 'safe':
        return concernLow;
      case 'moderate':
      case 'medium':
        return concernModerate;
      case 'high':
      case 'toxic':
      case 'hazardous':
        return concernHigh;
      default:
        return concernUnknown;
    }
  }

  static Color getConcernContainerColor(String? concernLevel) {
    switch (concernLevel?.toLowerCase().trim()) {
      case 'low':
      case 'safe':
        return concernLowContainer;
      case 'moderate':
      case 'medium':
        return concernModerateContainer;
      case 'high':
      case 'toxic':
      case 'hazardous':
        return concernHighContainer;
      default:
        return concernUnknownContainer;
    }
  }
}
