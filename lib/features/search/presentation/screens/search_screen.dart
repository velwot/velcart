import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../app/theme/app_text_styles.dart';
import '../../../../core/constants/app_constants.dart';
import '../../../../core/widgets/app_card.dart';
import '../../../../core/widgets/empty_state_view.dart';
import '../controllers/search_controller.dart';

class SearchScreen extends ConsumerStatefulWidget {
  const SearchScreen({super.key});

  @override
  ConsumerState<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends ConsumerState<SearchScreen> {
  final TextEditingController _textController = TextEditingController();

  @override
  void dispose() {
    _textController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(searchControllerProvider);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Search Products'),
      ),
      body: Column(
        children: [
          // Search Bar Input
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
            child: TextField(
              controller: _textController,
              textInputAction: TextInputAction.search,
              decoration: InputDecoration(
                hintText: 'Search by brand, name or barcode...',
                prefixIcon: const Icon(Icons.search_rounded),
                suffixIcon: _textController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear_rounded),
                        onPressed: () {
                          _textController.clear();
                          ref.read(searchControllerProvider.notifier).clearSearch();
                          setState(() {});
                        },
                      )
                    : null,
              ),
              onChanged: (val) {
                setState(() {});
              },
              onSubmitted: (query) {
                ref.read(searchControllerProvider.notifier).search(query);
              },
            ),
          ),

          // Search Body Content
          Expanded(
            child: state.isLoading
                ? const Center(child: CircularProgressIndicator())
                : state.results.isEmpty
                    ? EmptyStateView(
                        title: state.query.isEmpty
                            ? 'Search Product Catalog'
                            : 'No matching products found',
                        message: state.query.isEmpty
                            ? 'Enter a cosmetic, skincare, or personal care product name or brand to evaluate ingredients.'
                            : 'Remote catalog search will be integrated in subsequent prompts. You can also scan the barcode directly.',
                        icon: Icons.search_off_rounded,
                        actionLabel: 'Scan Barcode Instead',
                        onAction: () => context.push(AppConstants.scannerRoute),
                      )
                    : ListView.separated(
                        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                        itemCount: state.results.length,
                        separatorBuilder: (_, __) => const SizedBox(height: 8),
                        itemBuilder: (context, index) {
                          final product = state.results[index];
                          return AppCard(
                            onTap: () => context.push(
                              '${AppConstants.productRoute}?barcode=${product.barcode}',
                            ),
                            child: Row(
                              children: [
                                Container(
                                  width: 44,
                                  height: 44,
                                  decoration: BoxDecoration(
                                    color: theme.colorScheme.surfaceContainerHighest,
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                  child: const Icon(Icons.inventory_2_outlined),
                                ),
                                const SizedBox(width: 14),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(product.name, style: AppTextStyles.titleMedium),
                                      Text(
                                        '${product.brand} • ${product.category}',
                                        style: theme.textTheme.bodySmall,
                                      ),
                                    ],
                                  ),
                                ),
                                const Icon(Icons.chevron_right_rounded),
                              ],
                            ),
                          );
                        },
                      ),
          ),
        ],
      ),
    );
  }
}
