import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../domain/entities/product.dart';
import '../../../../domain/usecases/search_products_usecase.dart';
import '../../../../data/repositories/product_repository_impl.dart';

class SearchState {
  final bool isLoading;
  final String query;
  final List<Product> results;
  final String? errorMessage;

  const SearchState({
    this.isLoading = false,
    this.query = '',
    this.results = const [],
    this.errorMessage,
  });

  SearchState copyWith({
    bool? isLoading,
    String? query,
    List<Product>? results,
    String? errorMessage,
  }) {
    return SearchState(
      isLoading: isLoading ?? this.isLoading,
      query: query ?? this.query,
      results: results ?? this.results,
      errorMessage: errorMessage,
    );
  }
}

class ProductSearchController extends StateNotifier<SearchState> {
  final SearchProductsUseCase _searchProductsUseCase;

  ProductSearchController(this._searchProductsUseCase) : super(const SearchState());

  void setQuery(String query) {
    state = state.copyWith(query: query);
  }

  Future<void> search(String query) async {
    // In Prompt 1, remote search is deferred as requested
    state = state.copyWith(isLoading: true, query: query);
    try {
      final results = await _searchProductsUseCase(query);
      state = state.copyWith(isLoading: false, results: results);
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }

  void clearSearch() {
    state = const SearchState();
  }
}

final searchProductsUseCaseProvider = Provider<SearchProductsUseCase>((ref) {
  final repository = ref.watch(productRepositoryProvider);
  return SearchProductsUseCase(repository);
});

final searchControllerProvider =
    StateNotifierProvider<ProductSearchController, SearchState>((ref) {
  final useCase = ref.watch(searchProductsUseCaseProvider);
  return ProductSearchController(useCase);
});
