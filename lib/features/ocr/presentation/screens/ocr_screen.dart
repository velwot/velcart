import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../app/theme/app_text_styles.dart';
import '../../../../core/constants/app_constants.dart';
import '../../../../core/widgets/app_button.dart';
import '../../../../core/widgets/app_card.dart';
import '../controllers/ocr_controller.dart';

class OcrScreen extends ConsumerStatefulWidget {
  const OcrScreen({super.key});

  @override
  ConsumerState<OcrScreen> createState() => _OcrScreenState();
}

class _OcrScreenState extends ConsumerState<OcrScreen> {
  final TextEditingController _ingredientsInputController = TextEditingController();

  @override
  void dispose() {
    _ingredientsInputController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(ocrControllerProvider);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Scan Ingredient Label'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Label OCR Camera Viewfinder placeholder
            AppCard(
              backgroundColor: theme.colorScheme.surfaceContainerHighest,
              padding: const EdgeInsets.symmetric(vertical: 40, horizontal: 20),
              child: Center(
                child: Column(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: theme.colorScheme.primaryContainer,
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        Icons.document_scanner_rounded,
                        size: 44,
                        color: theme.colorScheme.primary,
                      ),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'Label OCR Optical Scanner',
                      style: AppTextStyles.titleMedium,
                    ),
                    const SizedBox(height: 6),
                    Text(
                      'Google ML Kit OCR engine architecture ready for camera recognition.',
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 16),
                    AppButton(
                      label: 'Capture Label Photo',
                      icon: Icons.camera_alt_outlined,
                      variant: AppButtonVariant.primary,
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                            content: Text('OCR Camera capture integration ready for Prompt 2.'),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 24),

            // Or Paste Ingredients Text Section
            Text('Or Paste Ingredient List', style: theme.textTheme.titleLarge),
            const SizedBox(height: 8),
            Text(
              'Paste ingredients from packaging or manufacturer website to check toxicity.',
              style: theme.textTheme.bodySmall?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 12),

            TextField(
              controller: _ingredientsInputController,
              maxLines: 5,
              decoration: const InputDecoration(
                hintText: 'e.g. Water, Glycerin, Methylparaben, Fragrance, Phenoxyethanol...',
                alignLabelWithHint: true,
              ),
            ),

            const SizedBox(height: 20),

            AppButton(
              label: 'Analyze Ingredients',
              icon: Icons.shield_outlined,
              variant: AppButtonVariant.primary,
              isFullWidth: true,
              isLoading: state.isProcessing,
              onPressed: () {
                final text = _ingredientsInputController.text.trim();
                if (text.isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Please enter or paste ingredients to analyze.'),
                    ),
                  );
                  return;
                }
                context.push(
                  '${AppConstants.analysisRoute}?ingredients=${Uri.encodeComponent(text)}',
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
