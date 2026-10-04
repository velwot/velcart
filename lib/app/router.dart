import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../core/constants/app_constants.dart';
import '../features/home/presentation/screens/home_screen.dart';
import '../features/scanner/presentation/screens/scanner_screen.dart';
import '../features/product/presentation/screens/product_screen.dart';
import '../features/analysis/presentation/screens/analysis_screen.dart';
import '../features/history/presentation/screens/history_screen.dart';
import '../features/search/presentation/screens/search_screen.dart';
import '../features/ocr/presentation/screens/ocr_screen.dart';
import '../features/settings/presentation/screens/settings_screen.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: AppConstants.homeRoute,
    routes: [
      GoRoute(
        path: AppConstants.homeRoute,
        name: 'home',
        builder: (context, state) => const HomeScreen(),
      ),
      GoRoute(
        path: AppConstants.scannerRoute,
        name: 'scanner',
        builder: (context, state) => const ScannerScreen(),
      ),
      GoRoute(
        path: AppConstants.productRoute,
        name: 'product',
        builder: (context, state) {
          final barcode = state.uri.queryParameters['barcode'] ?? '000000000000';
          return ProductScreen(barcode: barcode);
        },
      ),
      GoRoute(
        path: AppConstants.analysisRoute,
        name: 'analysis',
        builder: (context, state) {
          final ingredients = state.uri.queryParameters['ingredients'] ?? '';
          return AnalysisScreen(ingredientsText: ingredients);
        },
      ),
      GoRoute(
        path: AppConstants.historyRoute,
        name: 'history',
        builder: (context, state) => const HistoryScreen(),
      ),
      GoRoute(
        path: AppConstants.searchRoute,
        name: 'search',
        builder: (context, state) => const SearchScreen(),
      ),
      GoRoute(
        path: AppConstants.ocrRoute,
        name: 'ocr',
        builder: (context, state) => const OcrScreen(),
      ),
      GoRoute(
        path: AppConstants.settingsRoute,
        name: 'settings',
        builder: (context, state) => const SettingsScreen(),
      ),
    ],
    errorBuilder: (context, state) => Scaffold(
      body: Center(
        child: Text('Page not found: ${state.uri.toString()}'),
      ),
    ),
  );
});
