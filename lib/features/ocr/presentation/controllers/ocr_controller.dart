import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../services/ocr/ocr_service.dart';

class OcrState {
  final bool isProcessing;
  final String recognizedText;
  final String? errorMessage;

  const OcrState({
    this.isProcessing = false,
    this.recognizedText = '',
    this.errorMessage,
  });

  OcrState copyWith({
    bool? isProcessing,
    String? recognizedText,
    String? errorMessage,
  }) {
    return OcrState(
      isProcessing: isProcessing ?? this.isProcessing,
      recognizedText: recognizedText ?? this.recognizedText,
      errorMessage: errorMessage,
    );
  }
}

class OcrController extends StateNotifier<OcrState> {
  final OcrService _ocrService;

  OcrController(this._ocrService) : super(const OcrState());

  Future<void> processImage(String imagePath) async {
    state = state.copyWith(isProcessing: true, errorMessage: null);
    try {
      final text = await _ocrService.recognizeTextFromImagePath(imagePath);
      state = state.copyWith(isProcessing: false, recognizedText: text);
    } catch (e) {
      state = state.copyWith(isProcessing: false, errorMessage: e.toString());
    }
  }

  void setText(String text) {
    state = state.copyWith(recognizedText: text);
  }
}

final ocrControllerProvider =
    StateNotifierProvider<OcrController, OcrState>((ref) {
  final ocrService = ref.watch(ocrServiceProvider);
  return OcrController(ocrService);
});
