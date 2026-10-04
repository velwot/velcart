import '../entities/product.dart';
import '../repositories/product_repository.dart';
import '../../core/errors/app_exception.dart';

class GetProductByBarcodeUseCase {
  final ProductRepository _productRepository;

  GetProductByBarcodeUseCase(this._productRepository);

  Future<Product> call(String barcode) async {
    final cleanBarcode = barcode.trim();
    if (cleanBarcode.isEmpty) {
      throw const InvalidBarcodeException('', 'Barcode cannot be empty');
    }

    final product = await _productRepository.getProductByBarcode(cleanBarcode);
    if (product == null) {
      throw ProductNotFoundException(cleanBarcode);
    }
    return product;
  }
}
