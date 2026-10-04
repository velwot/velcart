import 'ingredient.dart';

class ProductAnalysis {
  final int score; // e.g. 0-100 or 1-10 (clean score)
  final int totalIngredients;
  final List<Ingredient> matchedIngredients;
  final List<Ingredient> flaggedIngredients;
  final List<String> unknownIngredients;
  final String summary;

  const ProductAnalysis({
    required this.score,
    required this.totalIngredients,
    required this.matchedIngredients,
    required this.flaggedIngredients,
    required this.unknownIngredients,
    required this.summary,
  });

  ProductAnalysis copyWith({
    int? score,
    int? totalIngredients,
    List<Ingredient>? matchedIngredients,
    List<Ingredient>? flaggedIngredients,
    List<String>? unknownIngredients,
    String? summary,
  }) {
    return ProductAnalysis(
      score: score ?? this.score,
      totalIngredients: totalIngredients ?? this.totalIngredients,
      matchedIngredients: matchedIngredients ?? this.matchedIngredients,
      flaggedIngredients: flaggedIngredients ?? this.flaggedIngredients,
      unknownIngredients: unknownIngredients ?? this.unknownIngredients,
      summary: summary ?? this.summary,
    );
  }

  @override
  String toString() =>
      'ProductAnalysis(score: $score, total: $totalIngredients, flagged: ${flaggedIngredients.length})';
}
