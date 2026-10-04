import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../dao/product_dao.dart';
import '../dao/scan_history_dao.dart';

abstract class LocalDatabaseService {
  ProductDao get productDao;
  ScanHistoryDao get scanHistoryDao;
  Future<void> init();
  Future<void> close();
}

class InMemoryLocalDatabase implements LocalDatabaseService {
  final ProductDao _productDao = ProductDao();
  final ScanHistoryDao _scanHistoryDao = ScanHistoryDao();

  @override
  ProductDao get productDao => _productDao;

  @override
  ScanHistoryDao get scanHistoryDao => _scanHistoryDao;

  @override
  Future<void> init() async {
    // Foundation initialization
  }

  @override
  Future<void> close() async {
    // Clean up
  }
}

final localDatabaseProvider = Provider<LocalDatabaseService>((ref) {
  final db = InMemoryLocalDatabase();
  ref.onDispose(() => db.close());
  return db;
});
