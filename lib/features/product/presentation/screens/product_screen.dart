import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../app/theme/app_text_styles.dart';
import '../../../../core/constants/app_constants.dart';
import '../../../../core/widgets/app_button.dart';
import '../../../../core/widgets/app_card.dart';
import '../../../../core/widgets/error_view.dart';
import '../../../../core/widgets/loading_view.dart';
import '../controllers/product_controller.dart';

class ProductScreen extends ConsumerStatefulWidget {
  final String barcode;

  const ProductScreen({super.key, required this.barcode});

  @override
  ConsumerState<ProductScreen> createState() => _ProductScreenState();
}

class _ProductScreenState extends ConsumerState<ProductScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() {
      ref.read(productControllerProvider.notifier).loadProduct(widget.barcode);
    });
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(productControllerProvider);
    final theme = Theme.of(context);

    if (state.isLoading) {
      return const Scaffold(
        body: LoadingView(message: 'Loading product details...'),
      );
    }

    if (state.errorMessage != null && state.product == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Product')),
        body: ErrorView(
          message: state.errorMessage!,
          onRetry: () =>
              ref.read(productControllerProvider.notifier).loadProduct(widget.barcode),
        ),
      );
    }

    final product = state.product;
    if (product == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Product')),
        body: const Center(child: Text('Product not found')),
      );
    }

    final ingredients = product.ingredientsText
        .split(RegExp(r'[,;•\n]'))
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Product Details'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Product Image / Header Card
            Center(
              child: Container(
                width: 160,
                height: 160,
                decoration: BoxDecoration(
                  color: theme.colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: theme.colorScheme.outline.withAlpha(50)),
                ),
                child: product.imageUrl != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(20),
                        child: Image.network(
                          product.imageUrl!,
                          fit: BoxFit.cover,
                          errorBuilder: (_, __, ___) => const Icon(
                            Icons.inventory_2_outlined,
                            size: 64,
                          ),
                        ),
                      )
                    : Icon(
                        Icons.inventory_2_outlined,
                        size: 64,
                        color: theme.colorScheme.primary,
                      ),
              ),
            ),
            const SizedBox(height: 20),

            // Product Name and Brand
            Text(
              product.name,
              style: AppTextStyles.headlineLarge,
            ),
            const SizedBox(height: 4),
            Text(
              product.brand,
              style: theme.textTheme.titleMedium?.copyWith(
                color: theme.colorScheme.primary,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),

            // Metadata Chips / Badges
            Row(
              children: [
                Chip(
                  label: Text(product.category),
                  avatar: const Icon(Icons.category_outlined, size: 16),
                  backgroundColor: theme.colorScheme.surfaceContainerHighest,
                  side: BorderSide.none,
                ),
                const SizedBox(width: 8),
                Chip(
                  label: Text('UPC: ${product.barcode}'),
                  avatar: const Icon(Icons.qr_code_2_rounded, size: 16),
                  backgroundColor: theme.colorScheme.surfaceContainerHighest,
                  side: BorderSide.none,
                ),
              ],
            ),

            const SizedBox(height: 24),

            // Analysis Callout Banner Button
            AppCard(
              backgroundColor: theme.colorScheme.primaryContainer.withAlpha(150),
              border: Border.all(color: theme.colorScheme.primary.withAlpha(80)),
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      color: theme.colorScheme.primary,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: const Icon(Icons.analytics_rounded, color: Colors.white),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Analyze Ingredients',
                          style: theme.textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.w700,
                            color: theme.colorScheme.onPrimaryContainer,
                          ),
                        ),
                        Text(
                          'View PureScan safety assessment & flagged toxicants.',
                          style: theme.textTheme.bodySmall?.copyWith(
                            color: theme.colorScheme.onPrimaryContainer.withAlpha(200),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              onTap: () {
                context.push(
                  '${AppConstants.analysisRoute}?ingredients=${Uri.encodeComponent(product.ingredientsText)}',
                );
              },
            ),

            const SizedBox(height: 24),

            // Ingredients List Section
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Ingredients (${ingredients.length})', style: theme.textTheme.titleLarge),
              ],
            ),
            const SizedBox(height: 12),
            AppCard(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    product.ingredientsText.isEmpty
                        ? 'No ingredients text found for this product.'
                        : product.ingredientsText,
                    style: theme.textTheme.bodyMedium?.copyWith(height: 1.6),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Bottom Full-width Action Button
            AppButton(
              label: 'View Detailed Analysis',
              icon: Icons.shield_outlined,
              variant: AppButtonVariant.primary,
              isFullWidth: true,
              onPressed: () {
                context.push(
                  '${AppConstants.analysisRoute}?ingredients=${Uri.encodeComponent(product.ingredientsText)}',
                );
              },
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
