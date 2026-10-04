import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../domain/entities/product.dart';
import '../../../../domain/usecases/get_product_by_barcode_usecase.dart';
import '../../../../data/repositories/product_repository_impl.dart';
import '../../../../core/errors/app_exception.dart';

class ProductState {
  final bool isLoading;
  final Product? product;
  final String? errorMessage;
  final bool isNotFound;

  const ProductState({
    this.isLoading = false,
    this.product,
    this.errorMessage,
    this.isNotFound = false,
  });

  ProductState copyWith({
    bool? isLoading,
    Product? product,
    String? errorMessage,
    bool? isNotFound,
  }) {
    return ProductState(
      isLoading: isLoading ?? this.isLoading,
      product: product ?? this.product,
      errorMessage: errorMessage,
      isNotFound: isNotFound ?? this.isNotFound,
    );
  }
}

class ProductController extends StateNotifier<ProductState> {
  final GetProductByBarcodeUseCase _getProductByBarcodeUseCase;

  ProductController(this._getProductByBarcodeUseCase) : super(const ProductState());

  Future<void> loadProduct(String barcode) async {
    state = state.copyWith(isLoading: true, errorMessage: null, isNotFound: false);
    try {
      final product = await _getProductByBarcodeUseCase(barcode);
      state = state.copyWith(isLoading: false, product: product, isNotFound: false);
    } on ProductNotFoundException catch (e) {
      state = state.copyWith(
        isLoading: false,
        product: null,
        errorMessage: 'Product with barcode $barcode was not found in the open product database.',
        isNotFound: true,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        product: null,
        errorMessage: e.toString(),
        isNotFound: false,
      );
    }
  }

  void setProduct(Product product) {
    state = state.copyWith(isLoading: false, product: product, errorMessage: null, isNotFound: false);
  }
}

final getProductByBarcodeUseCaseProvider = Provider<GetProductByBarcodeUseCase>((ref) {
  final repository = ref.watch(productRepositoryProvider);
  return GetProductByBarcodeUseCase(repository);
});

final productControllerProvider =
    StateNotifierProvider<ProductController, ProductState>((ref) {
  final useCase = ref.watch(getProductByBarcodeUseCaseProvider);
  return ProductController(useCase);
});
