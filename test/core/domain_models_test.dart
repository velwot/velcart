import 'package:flutter_test/flutter_test.dart';
import 'package:purescan/domain/entities/product.dart';
import 'package:purescan/domain/entities/ingredient.dart';
import 'package:purescan/domain/entities/product_analysis.dart';

void main() {
  group('Domain Models Test', () {
    test('Product entity creation and copyWith', () {
      const product = Product(
        barcode: '123456789012',
        name: 'Gentle Facial Wash',
        brand: 'PureCare',
        category: 'Skincare',
        ingredientsText: 'Aqua, Glycerin',
      );

      expect(product.barcode, '123456789012');
      expect(product.name, 'Gentle Facial Wash');
      expect(product.brand, 'PureCare');

      final updated = product.copyWith(name: 'Hydrating Facial Wash');
      expect(updated.name, 'Hydrating Facial Wash');
      expect(updated.barcode, '123456789012');
    });

    test('Ingredient entity creation and comparison', () {
      const ingredient1 = Ingredient(
        canonicalName: 'Parabens',
        aliases: ['Methylparaben', 'Propylparaben'],
        category: 'Preservative',
        concernLevel: 'high',
        description: 'Endocrine disruptor',
        evidenceSummary: 'Documented estrogenic activity',
        sources: ['EU SCCS'],
      );

      const ingredient2 = Ingredient(
        canonicalName: 'parabens',
        aliases: [],
        category: 'Preservative',
        concernLevel: 'high',
        description: 'Different description',
        evidenceSummary: '',
        sources: [],
      );

      expect(ingredient1, equals(ingredient2));
      expect(ingredient1.concernLevel, 'high');
    });

    test('ProductAnalysis model creation', () {
      const analysis = ProductAnalysis(
        score: 90,
        totalIngredients: 5,
        matchedIngredients: [],
        flaggedIngredients: [],
        unknownIngredients: ['Unknown Herb'],
        summary: 'Safe formulation',
      );

      expect(analysis.score, 90);
      expect(analysis.totalIngredients, 5);
      expect(analysis.unknownIngredients.length, 1);
    });
  });
}
