import '../../../domain/entities/product.dart';

class ProductDto {
  final String barcode;
  final String name;
  final String brand;
  final String? imageUrl;
  final String category;
  final String ingredientsText;

  const ProductDto({
    required this.barcode,
    required this.name,
    required this.brand,
    this.imageUrl,
    required this.category,
    required this.ingredientsText,
  });

  factory ProductDto.fromJson(Map<String, dynamic> json) {
    return ProductDto(
      barcode: json['barcode'] as String? ?? '',
      name: json['name'] as String? ?? json['product_name'] as String? ?? '',
      brand: json['brand'] as String? ?? json['brands'] as String? ?? '',
      imageUrl: json['image_url'] as String? ?? json['imageUrl'] as String?,
      category: json['category'] as String? ?? json['categories'] as String? ?? 'General',
      ingredientsText: json['ingredients_text'] as String? ?? json['ingredientsText'] as String? ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'barcode': barcode,
      'name': name,
      'brand': brand,
      'image_url': imageUrl,
      'category': category,
      'ingredients_text': ingredientsText,
    };
  }

  Product toDomain() {
    return Product(
      barcode: barcode,
      name: name,
      brand: brand,
      imageUrl: imageUrl,
      category: category,
      ingredientsText: ingredientsText,
    );
  }

  factory ProductDto.fromDomain(Product product) {
    return ProductDto(
      barcode: product.barcode,
      name: product.name,
      brand: product.brand,
      imageUrl: product.imageUrl,
      category: product.category,
      ingredientsText: product.ingredientsText,
    );
  }
}
