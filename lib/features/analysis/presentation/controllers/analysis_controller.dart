import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../domain/entities/product_analysis.dart';
import '../../../../domain/usecases/analyze_product_usecase.dart';
import '../../../../data/repositories/analysis_repository_impl.dart';

class AnalysisState {
  final bool isLoading;
  final ProductAnalysis? analysis;
  final String? errorMessage;

  const AnalysisState({
    this.isLoading = false,
    this.analysis,
    this.errorMessage,
  });

  AnalysisState copyWith({
    bool? isLoading,
    ProductAnalysis? analysis,
    String? errorMessage,
  }) {
    return AnalysisState(
      isLoading: isLoading ?? this.isLoading,
      analysis: analysis ?? this.analysis,
      errorMessage: errorMessage,
    );
  }
}

class AnalysisController extends StateNotifier<AnalysisState> {
  final AnalyzeProductUseCase _analyzeProductUseCase;

  AnalysisController(this._analyzeProductUseCase) : super(const AnalysisState());

  Future<void> analyze(String ingredientsText) async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    try {
      final result = await _analyzeProductUseCase(ingredientsText);
      state = state.copyWith(isLoading: false, analysis: result);
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }
}

final analyzeProductUseCaseProvider = Provider<AnalyzeProductUseCase>((ref) {
  final repository = ref.watch(analysisRepositoryProvider);
  return AnalyzeProductUseCase(repository);
});

final analysisControllerProvider =
    StateNotifierProvider<AnalysisController, AnalysisState>((ref) {
  final useCase = ref.watch(analyzeProductUseCaseProvider);
  return AnalysisController(useCase);
});
