import '../entities/product.dart';
import '../repositories/scan_history_repository.dart';

class GetScanHistoryUseCase {
  final ScanHistoryRepository _scanHistoryRepository;

  GetScanHistoryUseCase(this._scanHistoryRepository);

  Future<List<Product>> call({int limit = 20}) async {
    return await _scanHistoryRepository.getRecentScans(limit: limit);
  }
}
