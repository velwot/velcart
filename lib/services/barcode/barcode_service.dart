import 'package:flutter_riverpod/flutter_riverpod.dart';

abstract class BarcodeService {
  Future<bool> hasCameraPermission();
  Future<bool> requestCameraPermission();
}

class MobileScannerBarcodeService implements BarcodeService {
  @override
  Future<bool> hasCameraPermission() async {
    // Permission checking abstraction
    return true;
  }

  @override
  Future<bool> requestCameraPermission() async {
    return true;
  }
}

final barcodeServiceProvider = Provider<BarcodeService>((ref) {
  return MobileScannerBarcodeService();
});
