import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/constants/app_constants.dart';
import '../../domain/entities/ingredient.dart';
import '../../domain/entities/product_analysis.dart';
import '../../domain/repositories/analysis_repository.dart';
import '../remote/models/ingredient_dto.dart';

class AnalysisRepositoryImpl implements AnalysisRepository {
  List<Ingredient>? _cachedIngredients;

  @override
  Future<List<Ingredient>> getAllKnownIngredients() async {
    if (_cachedIngredients != null) {
      return _cachedIngredients!;
    }

    try {
      final jsonString = await rootBundle.loadString(AppConstants.ingredientsAssetPath);
      final List<dynamic> jsonList = json.decode(jsonString) as List<dynamic>;
      _cachedIngredients = jsonList
          .map((item) => IngredientDto.fromJson(item as Map<String, dynamic>).toDomain())
          .toList();
      return _cachedIngredients!;
    } catch (_) {
      return [];
    }
  }

  @override
  Future<Ingredient?> findIngredientByName(String name) async {
    final ingredients = await getAllKnownIngredients();
    final lower = name.trim().toLowerCase();
    for (final ing in ingredients) {
      if (ing.canonicalName.toLowerCase() == lower) {
        return ing;
      }
      for (final alias in ing.aliases) {
        if (alias.toLowerCase() == lower) {
          return ing;
        }
      }
    }
    return null;
  }

  @override
  Future<ProductAnalysis> analyzeIngredients(String ingredientsText) async {
    // Clean architecture foundation - scoring engine will be implemented in Prompt 2
    final rawTokens = ingredientsText
        .split(RegExp(r'[,;•\n]'))
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();

    final known = await getAllKnownIngredients();
    final List<Ingredient> matched = [];
    final List<Ingredient> flagged = [];
    final List<String> unknown = [];

    for (final token in rawTokens) {
      final lower = token.toLowerCase();
      Ingredient? match;
      for (final k in known) {
        if (k.canonicalName.toLowerCase() == lower ||
            k.aliases.any((a) => a.toLowerCase() == lower)) {
          match = k;
          break;
        }
      }

      if (match != null) {
        matched.add(match);
        if (match.concernLevel.toLowerCase() == 'high' ||
            match.concernLevel.toLowerCase() == 'moderate') {
          flagged.add(match);
        }
      } else {
        unknown.add(token);
      }
    }

    return ProductAnalysis(
      score: 85, // Neutral placeholder score for initial foundation
      totalIngredients: rawTokens.length,
      matchedIngredients: matched,
      flaggedIngredients: flagged,
      unknownIngredients: unknown,
      summary: 'PureScan analysis foundation prepared. Detailed toxicological scoring will be activated.',
    );
  }
}

final analysisRepositoryProvider = Provider<AnalysisRepository>((ref) {
  return AnalysisRepositoryImpl();
});
