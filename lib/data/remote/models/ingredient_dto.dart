import '../../../domain/entities/ingredient.dart';

class IngredientDto {
  final String canonicalName;
  final List<String> aliases;
  final String category;
  final String concernLevel;
  final String description;
  final String evidenceSummary;
  final List<String> sources;

  const IngredientDto({
    required this.canonicalName,
    required this.aliases,
    required this.category,
    required this.concernLevel,
    required this.description,
    required this.evidenceSummary,
    required this.sources,
  });

  factory IngredientDto.fromJson(Map<String, dynamic> json) {
    return IngredientDto(
      canonicalName: json['canonicalName'] as String? ?? '',
      aliases: (json['aliases'] as List<dynamic>?)?.map((e) => e.toString()).toList() ?? [],
      category: json['category'] as String? ?? 'General',
      concernLevel: json['concernLevel'] as String? ?? 'unknown',
      description: json['description'] as String? ?? '',
      evidenceSummary: json['evidenceSummary'] as String? ?? '',
      sources: (json['sources'] as List<dynamic>?)?.map((e) => e.toString()).toList() ?? [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'canonicalName': canonicalName,
      'aliases': aliases,
      'category': category,
      'concernLevel': concernLevel,
      'description': description,
      'evidenceSummary': evidenceSummary,
      'sources': sources,
    };
  }

  Ingredient toDomain() {
    return Ingredient(
      canonicalName: canonicalName,
      aliases: aliases,
      category: category,
      concernLevel: concernLevel,
      description: description,
      evidenceSummary: evidenceSummary,
      sources: sources,
    );
  }

  factory IngredientDto.fromDomain(Ingredient ingredient) {
    return IngredientDto(
      canonicalName: ingredient.canonicalName,
      aliases: ingredient.aliases,
      category: ingredient.category,
      concernLevel: ingredient.concernLevel,
      description: ingredient.description,
      evidenceSummary: ingredient.evidenceSummary,
      sources: ingredient.sources,
    );
  }
}
