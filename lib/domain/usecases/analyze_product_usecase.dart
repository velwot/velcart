import '../entities/product_analysis.dart';
import '../repositories/analysis_repository.dart';

class AnalyzeProductUseCase {
  final AnalysisRepository _analysisRepository;

  AnalyzeProductUseCase(this._analysisRepository);

  Future<ProductAnalysis> call(String ingredientsText) async {
    return await _analysisRepository.analyzeIngredients(ingredientsText);
  }
}
