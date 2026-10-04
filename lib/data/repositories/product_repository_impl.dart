import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../domain/entities/product.dart';
import '../../domain/repositories/product_repository.dart';
import '../local/database/app_database.dart';
import '../remote/api/product_api_service.dart';

class ProductRepositoryImpl implements ProductRepository {
  final LocalDatabaseService _localDb;
  final ProductApiService _apiService;

  ProductRepositoryImpl({
    required LocalDatabaseService localDb,
    required ProductApiService apiService,
  })  : _localDb = localDb,
        _apiService = apiService;

  @override
  Future<Product?> getProductByBarcode(String barcode) async {
    // Check local database first
    final localProduct = await _localDb.productDao.getProductByBarcode(barcode);
    if (localProduct != null) {
      return localProduct;
    }

    // Try remote API
    final remoteDto = await _apiService.fetchProductByBarcode(barcode);
    if (remoteDto != null) {
      final domain = remoteDto.toDomain();
      await _localDb.productDao.insertOrUpdate(domain);
      return domain;
    }

    return null;
  }

  @override
  Future<List<Product>> searchProducts(String query) async {
    final localResults = await _localDb.productDao.search(query);
    if (localResults.isNotEmpty) {
      return localResults;
    }

    final remoteDtos = await _apiService.searchProducts(query);
    return remoteDtos.map((dto) => dto.toDomain()).toList();
  }

  @override
  Future<void> saveProduct(Product product) async {
    await _localDb.productDao.insertOrUpdate(product);
  }
}

final productRepositoryProvider = Provider<ProductRepository>((ref) {
  final localDb = ref.watch(localDatabaseProvider);
  final apiService = ref.watch(productApiServiceProvider);
  return ProductRepositoryImpl(localDb: localDb, apiService: apiService);
});
