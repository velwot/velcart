class Product {
  final String barcode;
  final String name;
  final String brand;
  final String? imageUrl;
  final String category;
  final String ingredientsText;

  const Product({
    required this.barcode,
    required this.name,
    required this.brand,
    this.imageUrl,
    required this.category,
    required this.ingredientsText,
  });

  Product copyWith({
    String? barcode,
    String? name,
    String? brand,
    String? imageUrl,
    String? category,
    String? ingredientsText,
  }) {
    return Product(
      barcode: barcode ?? this.barcode,
      name: name ?? this.name,
      brand: brand ?? this.brand,
      imageUrl: imageUrl ?? this.imageUrl,
      category: category ?? this.category,
      ingredientsText: ingredientsText ?? this.ingredientsText,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Product &&
          runtimeType == other.runtimeType &&
          barcode == other.barcode;

  @override
  int get hashCode => barcode.hashCode;

  @override
  String toString() => 'Product(barcode: $barcode, name: $name, brand: $brand)';
}
