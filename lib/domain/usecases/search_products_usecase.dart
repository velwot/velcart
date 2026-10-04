import '../entities/product.dart';
import '../repositories/product_repository.dart';

class SearchProductsUseCase {
  final ProductRepository _productRepository;

  SearchProductsUseCase(this._productRepository);

  Future<List<Product>> call(String query) async {
    final cleanQuery = query.trim();
    if (cleanQuery.isEmpty) {
      return [];
    }
    return await _productRepository.searchProducts(cleanQuery);
  }
}
