import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../app/theme/app_colors.dart';
import '../../../../core/constants/app_constants.dart';
import '../../../../core/widgets/app_button.dart';
import '../../../../core/widgets/error_view.dart';
import '../controllers/scanner_controller.dart';

class ScannerScreen extends ConsumerStatefulWidget {
  const ScannerScreen({super.key});

  @override
  ConsumerState<ScannerScreen> createState() => _ScannerScreenState();
}

class _ScannerScreenState extends ConsumerState<ScannerScreen> {
  final TextEditingController _manualBarcodeController = TextEditingController();

  @override
  void dispose() {
    _manualBarcodeController.dispose();
    super.dispose();
  }

  void _showManualEntryDialog() {
    showDialog<void>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Enter Barcode Manually'),
          content: TextField(
            controller: _manualBarcodeController,
            keyboardType: TextInputType.number,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'UPC / EAN Barcode',
              hintText: 'e.g. 012345678905',
              prefixIcon: Icon(Icons.qr_code_2_rounded),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () {
                final barcode = _manualBarcodeController.text.trim();
                if (barcode.isNotEmpty) {
                  Navigator.of(dialogContext).pop();
                  context.push('${AppConstants.productRoute}?barcode=$barcode');
                }
              },
              child: const Text('Lookup'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(scannerControllerProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        foregroundColor: Colors.white,
        title: const Text('Scan Product Barcode'),
        actions: [
          IconButton(
            icon: Icon(
              state.isFlashOn ? Icons.flash_on_rounded : Icons.flash_off_rounded,
              color: state.isFlashOn ? AppColors.concernModerate : Colors.white,
            ),
            tooltip: 'Toggle Flash',
            onPressed: () => ref.read(scannerControllerProvider.notifier).toggleFlash(),
          ),
          IconButton(
            icon: const Icon(Icons.keyboard_alt_outlined),
            tooltip: 'Manual Entry',
            onPressed: _showManualEntryDialog,
          ),
        ],
      ),
      body: Stack(
        children: [
          // Camera Scanning Area / Viewport
          Positioned.fill(
            child: Container(
              color: Colors.black87,
              child: Center(
                child: state.status == ScannerCameraStatus.permissionDenied
                    ? ErrorView(
                        title: 'Camera Permission Denied',
                        message: state.errorMessage ??
                            'PureScan requires camera access to scan barcodes.',
                        icon: Icons.camera_alt_outlined,
                        retryLabel: 'Grant Permission',
                        onRetry: () =>
                            ref.read(scannerControllerProvider.notifier).requestPermission(),
                      )
                    : state.status == ScannerCameraStatus.checkingPermission
                        ? const CircularProgressIndicator(color: Colors.white)
                        : Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              // Viewfinder Target Frame
                              Container(
                                width: 260,
                                height: 260,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(20),
                                  border: Border.all(
                                    color: theme.colorScheme.primary,
                                    width: 3,
                                  ),
                                ),
                                child: Stack(
                                  children: [
                                    // Animated scan line or center guide
                                    Center(
                                      child: Container(
                                        height: 2,
                                        width: 220,
                                        color: theme.colorScheme.primary.withAlpha(200),
                                      ),
                                    ),
                                    Positioned(
                                      bottom: 16,
                                      left: 0,
                                      right: 0,
                                      child: Text(
                                        'Align barcode within frame',
                                        textAlign: TextAlign.center,
                                        style: theme.textTheme.bodySmall?.copyWith(
                                          color: Colors.white70,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              const SizedBox(height: 20),
                              Text(
                                'Barcode scanning engine ready',
                                style: theme.textTheme.bodyMedium?.copyWith(
                                  color: Colors.white70,
                                ),
                              ),
                            ],
                          ),
              ),
            ),
          ),

          // Bottom Bar for Manual Entry Option
          Positioned(
            left: 20,
            right: 20,
            bottom: 30,
            child: AppButton(
              label: 'Enter Barcode Manually',
              icon: Icons.keyboard_rounded,
              variant: AppButtonVariant.secondary,
              isFullWidth: true,
              onPressed: _showManualEntryDialog,
            ),
          ),
        ],
      ),
    );
  }
}
