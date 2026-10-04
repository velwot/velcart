import 'package:flutter_test/flutter_test.dart';
import 'package:purescan/domain/entities/product.dart';
import 'package:purescan/data/remote/models/product_dto.dart';

void main() {
  group('Product Entity and DTO Tests', () {
    test('ProductDto converts to domain Product accurately', () {
      const dto = ProductDto(
        barcode: '3600523723379',
        name: 'Micellar Cleansing Water',
        brand: "L'Oréal",
        category: 'Skincare',
        imageUrl: 'https://example.com/img.jpg',
        ingredientsText: 'Aqua, Hexylene Glycol, Glycerin, Disodium Cocoamphodiacetate',
      );

      final domain = dto.toDomain();

      expect(domain.barcode, equals('3600523723379'));
      expect(domain.name, equals('Micellar Cleansing Water'));
      expect(domain.brand, equals("L'Oréal"));
      expect(domain.category, equals('Skincare'));
      expect(domain.ingredientsText, contains('Aqua'));
    });

    test('ProductDto parses JSON from Open Beauty Facts format', () {
      final json = {
        'barcode': '3600523723379',
        'product_name': 'Micellar Cleansing Water',
        'brands': "L'Oréal",
        'categories': 'Skincare, Cleanser',
        'image_url': 'https://example.com/img.jpg',
        'ingredients_text': 'Aqua, Glycerin, Disodium EDTA',
      };

      final dto = ProductDto.fromJson(json);

      expect(dto.barcode, equals('3600523723379'));
      expect(dto.name, equals('Micellar Cleansing Water'));
      expect(dto.brand, equals("L'Oréal"));
      expect(dto.ingredientsText, equals('Aqua, Glycerin, Disodium EDTA'));
    });
  });
}
