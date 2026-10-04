import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../domain/entities/product.dart';
import '../../../../domain/usecases/get_scan_history_usecase.dart';
import '../../../../data/repositories/scan_history_repository_impl.dart';

class HistoryState {
  final bool isLoading;
  final List<Product> history;
  final String? errorMessage;

  const HistoryState({
    this.isLoading = false,
    this.history = const [],
    this.errorMessage,
  });

  HistoryState copyWith({
    bool? isLoading,
    List<Product>? history,
    String? errorMessage,
  }) {
    return HistoryState(
      isLoading: isLoading ?? this.isLoading,
      history: history ?? this.history,
      errorMessage: errorMessage,
    );
  }
}

class HistoryController extends StateNotifier<HistoryState> {
  final GetScanHistoryUseCase _getScanHistoryUseCase;

  HistoryController(this._getScanHistoryUseCase) : super(const HistoryState()) {
    loadHistory();
  }

  Future<void> loadHistory() async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    try {
      final items = await _getScanHistoryUseCase(limit: 50);
      state = state.copyWith(isLoading: false, history: items);
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }

  Future<void> clearHistory() async {
    state = state.copyWith(history: []);
  }
}

final historyScanUseCaseProvider = Provider<GetScanHistoryUseCase>((ref) {
  final repository = ref.watch(scanHistoryRepositoryProvider);
  return GetScanHistoryUseCase(repository);
});

final historyControllerProvider =
    StateNotifierProvider<HistoryController, HistoryState>((ref) {
  final useCase = ref.watch(historyScanUseCaseProvider);
  return HistoryController(useCase);
});
