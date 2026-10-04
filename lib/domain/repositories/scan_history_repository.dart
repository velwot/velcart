import '../entities/product.dart';

abstract class ScanHistoryRepository {
  Future<List<Product>> getRecentScans({int limit = 20});
  Future<void> addToHistory(Product product);
  Future<void> clearHistory();
  Future<void> removeFromHistory(String barcode);
}
