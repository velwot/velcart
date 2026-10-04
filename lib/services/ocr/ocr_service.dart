import 'package:flutter_riverpod/flutter_riverpod.dart';

abstract class OcrService {
  Future<String> recognizeTextFromImagePath(String imagePath);
}

class MlKitOcrService implements OcrService {
  @override
  Future<String> recognizeTextFromImagePath(String imagePath) async {
    // OCR pipeline abstraction for future Prompt implementations
    return '';
  }
}

final ocrServiceProvider = Provider<OcrService>((ref) {
  return MlKitOcrService();
});
