import 'package:flutter_riverpod/flutter_riverpod.dart';

abstract class AiService {
  Future<String> summarizeIngredientImpact(String ingredientName);
}

class GeminiAiService implements AiService {
  @override
  Future<String> summarizeIngredientImpact(String ingredientName) async {
    return 'Summary for $ingredientName will be provided by AI service.';
  }
}

final aiServiceProvider = Provider<AiService>((ref) {
  return GeminiAiService();
});
