import '../entities/product_analysis.dart';
import '../entities/ingredient.dart';

abstract class AnalysisRepository {
  Future<ProductAnalysis> analyzeIngredients(String ingredientsText);
  Future<List<Ingredient>> getAllKnownIngredients();
  Future<Ingredient?> findIngredientByName(String name);
}
