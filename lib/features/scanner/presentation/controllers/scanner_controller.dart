import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../services/barcode/barcode_service.dart';

enum ScannerCameraStatus { initial, checkingPermission, permissionGranted, permissionDenied, scanning, error }

class ScannerState {
  final ScannerCameraStatus status;
  final String? errorMessage;
  final bool isFlashOn;

  const ScannerState({
    this.status = ScannerCameraStatus.initial,
    this.errorMessage,
    this.isFlashOn = false,
  });

  ScannerState copyWith({
    ScannerCameraStatus? status,
    String? errorMessage,
    bool? isFlashOn,
  }) {
    return ScannerState(
      status: status ?? this.status,
      errorMessage: errorMessage,
      isFlashOn: isFlashOn ?? this.isFlashOn,
    );
  }
}

class ScannerController extends StateNotifier<ScannerState> {
  final BarcodeService _barcodeService;

  ScannerController(this._barcodeService) : super(const ScannerState()) {
    checkPermission();
  }

  Future<void> checkPermission() async {
    state = state.copyWith(status: ScannerCameraStatus.checkingPermission);
    try {
      final granted = await _barcodeService.hasCameraPermission();
      if (granted) {
        state = state.copyWith(status: ScannerCameraStatus.permissionGranted);
      } else {
        state = state.copyWith(
          status: ScannerCameraStatus.permissionDenied,
          errorMessage: 'Camera permission is required to scan product barcodes.',
        );
      }
    } catch (e) {
      state = state.copyWith(
        status: ScannerCameraStatus.error,
        errorMessage: 'Unable to access camera: $e',
      );
    }
  }

  Future<void> requestPermission() async {
    state = state.copyWith(status: ScannerCameraStatus.checkingPermission);
    try {
      final granted = await _barcodeService.requestCameraPermission();
      if (granted) {
        state = state.copyWith(status: ScannerCameraStatus.permissionGranted);
      } else {
        state = state.copyWith(
          status: ScannerCameraStatus.permissionDenied,
          errorMessage: 'Camera permission was denied.',
        );
      }
    } catch (e) {
      state = state.copyWith(
        status: ScannerCameraStatus.error,
        errorMessage: 'Permission request failed: $e',
      );
    }
  }

  void toggleFlash() {
    state = state.copyWith(isFlashOn: !state.isFlashOn);
  }
}

final scannerControllerProvider =
    StateNotifierProvider<ScannerController, ScannerState>((ref) {
  final barcodeService = ref.watch(barcodeServiceProvider);
  return ScannerController(barcodeService);
});
