import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../domain/entities/product.dart';
import '../../domain/repositories/scan_history_repository.dart';
import '../local/database/app_database.dart';

class ScanHistoryRepositoryImpl implements ScanHistoryRepository {
  final LocalDatabaseService _localDb;

  ScanHistoryRepositoryImpl(this._localDb);

  @override
  Future<List<Product>> getRecentScans({int limit = 20}) async {
    final entries = await _localDb.scanHistoryDao.getRecent(limit: limit);
    final List<Product> products = [];
    for (final entry in entries) {
      final product = await _localDb.productDao.getProductByBarcode(entry.barcode);
      if (product != null) {
        products.add(product);
      }
    }
    return products;
  }

  @override
  Future<void> addToHistory(Product product) async {
    await _localDb.productDao.insertOrUpdate(product);
    await _localDb.scanHistoryDao.add(product.barcode);
  }

  @override
  Future<void> removeFromHistory(String barcode) async {
    await _localDb.scanHistoryDao.remove(barcode);
  }

  @override
  Future<void> clearHistory() async {
    await _localDb.scanHistoryDao.clear();
  }
}

final scanHistoryRepositoryProvider = Provider<ScanHistoryRepository>((ref) {
  final localDb = ref.watch(localDatabaseProvider);
  return ScanHistoryRepositoryImpl(localDb);
});
