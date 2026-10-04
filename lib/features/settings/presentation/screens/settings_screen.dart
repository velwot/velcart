import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/constants/app_constants.dart';
import '../../../../core/widgets/app_card.dart';
import '../controllers/settings_controller.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  void _showClearHistoryDialog(BuildContext context, WidgetRef ref) {
    showDialog<void>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Clear Scan History?'),
          content: const Text(
            'This will remove all your locally cached scans from this device. This action cannot be undone.',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () async {
                Navigator.of(dialogContext).pop();
                await ref.read(settingsControllerProvider.notifier).clearHistory();
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Scan history cleared successfully.')),
                  );
                }
              },
              child: const Text('Clear'),
            ),
          ],
        );
      },
    );
  }

  void _showPrivacyDialog(BuildContext context) {
    showDialog<void>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Privacy & Independence'),
          content: const SingleChildScrollView(
            child: Text(
              'PureScan is an independent personal-use tool designed for personal ingredient safety.\n\n'
              '• No Trackers: PureScan does not sell your personal data or browsing behavior to manufacturers.\n'
              '• On-Device Evaluation: Ingredient searches and analysis prioritization run locally wherever possible.\n'
              '• Secure Architecture: API keys are maintained via secure environment configurations without hardcoding.',
            ),
          ),
          actions: [
            FilledButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('Close'),
            ),
          ],
        );
      },
    );
  }

  void _showAboutDialog(BuildContext context) {
    showAboutDialog(
      context: context,
      applicationName: AppConstants.appName,
      applicationVersion: AppConstants.appVersion,
      applicationIcon: const Icon(Icons.eco_rounded, size: 40, color: Color(0xFF0F766E)),
      children: const [
        SizedBox(height: 12),
        Text(
          'PureScan empowers individuals to evaluate ingredients in everyday cosmetics, food, and personal care products without manufacturer bias.',
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(settingsControllerProvider);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        children: [
          // Theme Section
          Text('Appearance', style: theme.textTheme.titleMedium),
          const SizedBox(height: 10),
          AppCard(
            padding: const EdgeInsets.all(8),
            child: Column(
              children: [
                RadioListTile<ThemeMode>(
                  title: const Text('System Default'),
                  secondary: const Icon(Icons.brightness_auto_rounded),
                  value: ThemeMode.system,
                  groupValue: state.themeMode,
                  onChanged: (mode) {
                    if (mode != null) {
                      ref.read(settingsControllerProvider.notifier).setThemeMode(mode);
                    }
                  },
                ),
                RadioListTile<ThemeMode>(
                  title: const Text('Light'),
                  secondary: const Icon(Icons.light_mode_outlined),
                  value: ThemeMode.light,
                  groupValue: state.themeMode,
                  onChanged: (mode) {
                    if (mode != null) {
                      ref.read(settingsControllerProvider.notifier).setThemeMode(mode);
                    }
                  },
                ),
                RadioListTile<ThemeMode>(
                  title: const Text('Dark'),
                  secondary: const Icon(Icons.dark_mode_outlined),
                  value: ThemeMode.dark,
                  groupValue: state.themeMode,
                  onChanged: (mode) {
                    if (mode != null) {
                      ref.read(settingsControllerProvider.notifier).setThemeMode(mode);
                    }
                  },
                ),
              ],
            ),
          ),

          const SizedBox(height: 24),

          // Data Management Section
          Text('Data & Storage', style: theme.textTheme.titleMedium),
          const SizedBox(height: 10),
          AppCard(
            padding: EdgeInsets.zero,
            child: ListTile(
              leading: const Icon(Icons.delete_outline_rounded, color: Colors.red),
              title: const Text('Clear Scan History', style: TextStyle(color: Colors.red)),
              subtitle: const Text('Delete local scan records from device'),
              trailing: state.isClearingHistory
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Icon(Icons.chevron_right_rounded),
              onTap: state.isClearingHistory ? null : () => _showClearHistoryDialog(context, ref),
            ),
          ),

          const SizedBox(height: 24),

          // Information & Legal Section
          Text('About & Privacy', style: theme.textTheme.titleMedium),
          const SizedBox(height: 10),
          AppCard(
            padding: EdgeInsets.zero,
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.privacy_tip_outlined),
                  title: const Text('Privacy Information'),
                  subtitle: const Text('Data transparency and permissions'),
                  trailing: const Icon(Icons.chevron_right_rounded),
                  onTap: () => _showPrivacyDialog(context),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.info_outline_rounded),
                  title: const Text('About PureScan'),
                  subtitle: const Text('Version ${AppConstants.appVersion}'),
                  trailing: const Icon(Icons.chevron_right_rounded),
                  onTap: () => _showAboutDialog(context),
                ),
              ],
            ),
          ),

          const SizedBox(height: 32),

          Center(
            child: Text(
              'PureScan • Personal Toxicant & Ingredient Analysis',
              style: theme.textTheme.bodySmall?.copyWith(
                color: theme.colorScheme.onSurfaceVariant.withAlpha(150),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
