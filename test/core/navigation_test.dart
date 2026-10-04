import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:purescan/app/app.dart';

void main() {
  testWidgets('Navigation and Home Screen initial render test', (WidgetTester tester) async {
    await tester.pumpWidget(
      const ProviderScope(
        child: PureScanApp(),
      ),
    );

    await tester.pumpAndSettle();

    // Verify PureScan branding and buttons exist
    expect(find.text('PureScan'), findsWidgets);
    expect(find.text('Scan Product Barcode'), findsOneWidget);
    expect(find.text('Search Product Catalog'), findsOneWidget);
    expect(find.text('Scan Ingredient Label (OCR)'), findsOneWidget);
  });
}
