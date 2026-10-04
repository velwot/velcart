import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../app/theme/app_colors.dart';
import '../../../../app/theme/app_text_styles.dart';
import '../../../../core/widgets/app_card.dart';
import '../../../../core/widgets/error_view.dart';
import '../../../../core/widgets/loading_view.dart';
import '../controllers/analysis_controller.dart';

class AnalysisScreen extends ConsumerStatefulWidget {
  final String ingredientsText;

  const AnalysisScreen({super.key, required this.ingredientsText});

  @override
  ConsumerState<AnalysisScreen> createState() => _AnalysisScreenState();
}

class _AnalysisScreenState extends ConsumerState<AnalysisScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() {
      ref.read(analysisControllerProvider.notifier).analyze(widget.ingredientsText);
    });
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(analysisControllerProvider);
    final theme = Theme.of(context);

    if (state.isLoading) {
      return const Scaffold(
        body: LoadingView(message: 'Analyzing ingredients for toxicological risks...'),
      );
    }

    if (state.errorMessage != null) {
      return Scaffold(
        appBar: AppBar(title: const Text('PureScan Analysis')),
        body: ErrorView(
          message: state.errorMessage!,
          onRetry: () =>
              ref.read(analysisControllerProvider.notifier).analyze(widget.ingredientsText),
        ),
      );
    }

    final analysis = state.analysis;
    if (analysis == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('PureScan Analysis')),
        body: const Center(child: Text('No analysis data available.')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('PureScan Safety Analysis'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Score Header Hero Card
            AppCard(
              backgroundColor: theme.colorScheme.surface,
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  Row(
                    children: [
                      // Score Circular Badge
                      Container(
                        width: 72,
                        height: 72,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: AppColors.primaryContainer,
                          border: Border.all(color: theme.colorScheme.primary, width: 3),
                        ),
                        child: Center(
                          child: Text(
                            '${analysis.score}',
                            style: AppTextStyles.displayMedium.copyWith(
                              color: theme.colorScheme.primary,
                              fontWeight: FontWeight.w800,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 20),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'PureScan Score',
                              style: theme.textTheme.titleMedium?.copyWith(
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Scale 0-100 (Higher indicates cleaner formulation). Scoring engine ready for Prompt 2.',
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: theme.colorScheme.onSurfaceVariant,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  const Divider(height: 1),
                  const SizedBox(height: 16),

                  // Metrics Breakdown Row
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _MetricItem(
                        label: 'Total',
                        count: analysis.totalIngredients,
                        color: theme.colorScheme.onSurface,
                      ),
                      _MetricItem(
                        label: 'Matched',
                        count: analysis.matchedIngredients.length,
                        color: AppColors.concernLow,
                      ),
                      _MetricItem(
                        label: 'Flagged',
                        count: analysis.flaggedIngredients.length,
                        color: AppColors.concernHigh,
                      ),
                      _MetricItem(
                        label: 'Unknown',
                        count: analysis.unknownIngredients.length,
                        color: AppColors.concernUnknown,
                      ),
                    ],
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Flagged Ingredients Section
            if (analysis.flaggedIngredients.isNotEmpty) ...[
              Text(
                'Flagged Ingredients (${analysis.flaggedIngredients.length})',
                style: theme.textTheme.titleLarge?.copyWith(
                  color: AppColors.concernHigh,
                ),
              ),
              const SizedBox(height: 12),
              ...analysis.flaggedIngredients.map(
                (ing) => AppCard(
                  margin: const EdgeInsets.only(bottom: 12),
                  backgroundColor: AppColors.concernHighContainer.withAlpha(50),
                  border: Border.all(color: AppColors.concernHigh.withAlpha(100)),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(
                            child: Text(
                              ing.canonicalName,
                              style: AppTextStyles.titleMedium.copyWith(
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                          ),
                          Chip(
                            label: Text(ing.concernLevel.toUpperCase()),
                            backgroundColor: AppColors.getConcernColor(ing.concernLevel),
                            labelStyle: const TextStyle(
                              color: Colors.white,
                              fontSize: 10,
                              fontWeight: FontWeight.bold,
                            ),
                            padding: EdgeInsets.zero,
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        ing.description,
                        style: theme.textTheme.bodyMedium,
                      ),
                      if (ing.evidenceSummary.isNotEmpty) ...[
                        const SizedBox(height: 8),
                        Text(
                          'Evidence: ${ing.evidenceSummary}',
                          style: theme.textTheme.bodySmall?.copyWith(
                            fontStyle: FontStyle.italic,
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],

            // Matched Safe Ingredients Section
            if (analysis.matchedIngredients.any((i) => i.concernLevel.toLowerCase() == 'low')) ...[
              Text('Safe / Verified Ingredients', style: theme.textTheme.titleLarge),
              const SizedBox(height: 12),
              ...analysis.matchedIngredients
                  .where((i) => i.concernLevel.toLowerCase() == 'low')
                  .map(
                    (ing) => AppCard(
                      margin: const EdgeInsets.only(bottom: 10),
                      child: Row(
                        children: [
                          const Icon(Icons.check_circle_outline_rounded,
                              color: AppColors.concernLow, size: 22),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(ing.canonicalName, style: AppTextStyles.titleMedium),
                                Text(
                                  ing.category,
                                  style: theme.textTheme.bodySmall?.copyWith(
                                    color: theme.colorScheme.onSurfaceVariant,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
              const SizedBox(height: 16),
            ],

            // Unknown Ingredients Section
            if (analysis.unknownIngredients.isNotEmpty) ...[
              Text(
                'Unindexed Ingredients (${analysis.unknownIngredients.length})',
                style: theme.textTheme.titleLarge,
              ),
              const SizedBox(height: 8),
              Text(
                'These terms were not yet cataloged in the local database.',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: analysis.unknownIngredients
                    .map(
                      (name) => Chip(
                        label: Text(name),
                        backgroundColor: theme.colorScheme.surfaceContainerHighest,
                        side: BorderSide.none,
                      ),
                    )
                    .toList(),
              ),
              const SizedBox(height: 24),
            ],
          ],
        ),
      ),
    );
  }
}

class _MetricItem extends StatelessWidget {
  final String label;
  final int count;
  final Color color;

  const _MetricItem({
    required this.label,
    required this.count,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          '$count',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.w700,
            color: color,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
                fontWeight: FontWeight.w500,
              ),
        ),
      ],
    );
  }
}
