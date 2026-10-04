import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../domain/entities/product.dart';
import '../../../../domain/usecases/get_scan_history_usecase.dart';
import '../../../../data/repositories/scan_history_repository_impl.dart';

class HomeState {
  final bool isLoading;
  final List<Product> recentScans;
  final String? errorMessage;

  const HomeState({
    this.isLoading = false,
    this.recentScans = const [],
    this.errorMessage,
  });

  HomeState copyWith({
    bool? isLoading,
    List<Product>? recentScans,
    String? errorMessage,
  }) {
    return HomeState(
      isLoading: isLoading ?? this.isLoading,
      recentScans: recentScans ?? this.recentScans,
      errorMessage: errorMessage,
    );
  }
}

class HomeController extends StateNotifier<HomeState> {
  final GetScanHistoryUseCase _getScanHistoryUseCase;

  HomeController(this._getScanHistoryUseCase) : super(const HomeState()) {
    loadRecentScans();
  }

  Future<void> loadRecentScans() async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    try {
      final scans = await _getScanHistoryUseCase(limit: 5);
      state = state.copyWith(isLoading: false, recentScans: scans);
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }
}

final getScanHistoryUseCaseProvider = Provider<GetScanHistoryUseCase>((ref) {
  final repository = ref.watch(scanHistoryRepositoryProvider);
  return GetScanHistoryUseCase(repository);
});

final homeControllerProvider =
    StateNotifierProvider<HomeController, HomeState>((ref) {
  final useCase = ref.watch(getScanHistoryUseCaseProvider);
  return HomeController(useCase);
});
