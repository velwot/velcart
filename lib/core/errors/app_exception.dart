sealed class AppException implements Exception {
  final String message;
  final String? code;
  final dynamic details;

  const AppException(this.message, {this.code, this.details});

  @override
  String toString() => 'AppException(code: $code, message: $message)';
}

class NetworkFailureException extends AppException {
  const NetworkFailureException([super.message = 'Network connection failed. Please check your internet connection.', dynamic details])
      : super(code: 'NETWORK_FAILURE', details: details);
}

class ProductNotFoundException extends AppException {
  final String barcode;
  const ProductNotFoundException(this.barcode, [super.message = 'Product not found in our catalog.', dynamic details])
      : super(code: 'PRODUCT_NOT_FOUND', details: details);
}

class InvalidBarcodeException extends AppException {
  final String barcode;
  const InvalidBarcodeException(this.barcode, [super.message = 'The scanned barcode is invalid.', dynamic details])
      : super(code: 'INVALID_BARCODE', details: details);
}

class DatabaseErrorException extends AppException {
  const DatabaseErrorException([super.message = 'Local database operation failed.', dynamic details])
      : super(code: 'DATABASE_ERROR', details: details);
}

class CameraPermissionException extends AppException {
  const CameraPermissionException([super.message = 'Camera permission was denied. PureScan requires camera access to scan barcodes.', dynamic details])
      : super(code: 'CAMERA_PERMISSION_DENIED', details: details);
}

class UnknownErrorException extends AppException {
  const UnknownErrorException([super.message = 'An unexpected error occurred. Please try again.', dynamic details])
      : super(code: 'UNKNOWN_ERROR', details: details);
}
