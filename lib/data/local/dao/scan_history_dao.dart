class ScanHistoryEntry {
  final String barcode;
  final DateTime scannedAt;

  const ScanHistoryEntry({required this.barcode, required this.scannedAt});
}

class ScanHistoryDao {
  final List<ScanHistoryEntry> _history = [];

  Future<List<ScanHistoryEntry>> getRecent({int limit = 20}) async {
    final sorted = List<ScanHistoryEntry>.from(_history)
      ..sort((a, b) => b.scannedAt.compareTo(a.scannedAt));
    return sorted.take(limit).toList();
  }

  Future<void> add(String barcode) async {
    _history.removeWhere((item) => item.barcode == barcode);
    _history.insert(0, ScanHistoryEntry(barcode: barcode, scannedAt: DateTime.now()));
  }

  Future<void> remove(String barcode) async {
    _history.removeWhere((item) => item.barcode == barcode);
  }

  Future<void> clear() async {
    _history.clear();
  }
}
