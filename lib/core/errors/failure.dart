sealed class Failure {
  final String message;
  final String? code;

  const Failure(this.message, {this.code});

  @override
  String toString() => '$runtimeType: $message (code: $code)';
}

class NetworkFailure extends Failure {
  const NetworkFailure([super.message = 'Network connection failure'])
      : super(code: 'NETWORK_FAILURE');
}

class ProductNotFoundFailure extends Failure {
  const ProductNotFoundFailure([super.message = 'Product not found'])
      : super(code: 'PRODUCT_NOT_FOUND');
}

class InvalidBarcodeFailure extends Failure {
  const InvalidBarcodeFailure([super.message = 'Invalid barcode format'])
      : super(code: 'INVALID_BARCODE');
}

class DatabaseFailure extends Failure {
  const DatabaseFailure([super.message = 'Database error'])
      : super(code: 'DATABASE_ERROR');
}

class CameraPermissionFailure extends Failure {
  const CameraPermissionFailure([super.message = 'Camera permission denied'])
      : super(code: 'CAMERA_PERMISSION');
}

class UnknownFailure extends Failure {
  const UnknownFailure([super.message = 'An unexpected failure occurred'])
      : super(code: 'UNKNOWN_FAILURE');
}
