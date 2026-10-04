import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:purescan/features/analysis/presentation/screens/analysis_screen.dart';
import 'package:purescan/core/widgets/error_view.dart';

void main() {
  group('Analysis Screen & Error Rendering Tests', () {
    testWidgets('Renders score placeholder screen', (WidgetTester tester) async {
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AnalysisScreen(ingredientsText: 'Aqua, Glycerin'),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('PureScan Safety Analysis'), findsOneWidget);
      expect(find.text('PureScan Score'), findsOneWidget);
      expect(find.text('Total'), findsOneWidget);
      expect(find.text('Matched'), findsOneWidget);
    });

    testWidgets('Renders ErrorView properly with retry callback', (WidgetTester tester) async {
      bool retried = false;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: ErrorView(
              title: 'Network Failure',
              message: 'Unable to reach servers',
              onRetry: () {
                retried = true;
              },
            ),
          ),
        ),
      );

      expect(find.text('Network Failure'), findsOneWidget);
      expect(find.text('Unable to reach servers'), findsOneWidget);
      expect(find.text('Try Again'), findsOneWidget);

      await tester.tap(find.text('Try Again'));
      expect(retried, isTrue);
    });
  });
}
