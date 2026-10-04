import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:purescan/features/scanner/presentation/screens/scanner_screen.dart';

void main() {
  testWidgets('Scanner screen renders viewfinder and manual entry option', (WidgetTester tester) async {
    await tester.pumpWidget(
      const ProviderScope(
        child: MaterialApp(
          home: ScannerScreen(),
        ),
      ),
    );

    await tester.pumpAndSettle();

    expect(find.text('Scan Product Barcode'), findsOneWidget);
    expect(find.text('Enter Barcode Manually'), findsOneWidget);
  });
}
