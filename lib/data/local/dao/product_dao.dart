import '../../../domain/entities/product.dart';

class ProductDao {
  final Map<String, Product> _store = {};

  Future<Product?> getProductByBarcode(String barcode) async {
    return _store[barcode];
  }

  Future<void> insertOrUpdate(Product product) async {
    _store[product.barcode] = product;
  }

  Future<List<Product>> search(String query) async {
    final lower = query.toLowerCase();
    return _store.values.where((p) {
      return p.name.toLowerCase().contains(lower) ||
          p.brand.toLowerCase().contains(lower) ||
          p.barcode.contains(lower);
    }).toList();
  }

  Future<void> delete(String barcode) async {
    _store.remove(barcode);
  }

  Future<void> clear() async {
    _store.clear();
  }
}
