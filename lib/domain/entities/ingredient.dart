class Ingredient {
  final String canonicalName;
  final List<String> aliases;
  final String category;
  final String concernLevel; // 'low', 'moderate', 'high', 'unknown'
  final String description;
  final String evidenceSummary;
  final List<String> sources;

  const Ingredient({
    required this.canonicalName,
    required this.aliases,
    required this.category,
    required this.concernLevel,
    required this.description,
    required this.evidenceSummary,
    required this.sources,
  });

  Ingredient copyWith({
    String? canonicalName,
    List<String>? aliases,
    String? category,
    String? concernLevel,
    String? description,
    String? evidenceSummary,
    List<String>? sources,
  }) {
    return Ingredient(
      canonicalName: canonicalName ?? this.canonicalName,
      aliases: aliases ?? this.aliases,
      category: category ?? this.category,
      concernLevel: concernLevel ?? this.concernLevel,
      description: description ?? this.description,
      evidenceSummary: evidenceSummary ?? this.evidenceSummary,
      sources: sources ?? this.sources,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Ingredient &&
          runtimeType == other.runtimeType &&
          canonicalName.toLowerCase() == other.canonicalName.toLowerCase();

  @override
  int get hashCode => canonicalName.toLowerCase().hashCode;

  @override
  String toString() => 'Ingredient(canonicalName: $canonicalName, concernLevel: $concernLevel)';
}
