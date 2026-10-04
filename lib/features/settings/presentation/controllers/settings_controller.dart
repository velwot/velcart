import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../data/repositories/scan_history_repository_impl.dart';

class SettingsState {
  final ThemeMode themeMode;
  final bool isClearingHistory;

  const SettingsState({
    this.themeMode = ThemeMode.system,
    this.isClearingHistory = false,
  });

  SettingsState copyWith({
    ThemeMode? themeMode,
    bool? isClearingHistory,
  }) {
    return SettingsState(
      themeMode: themeMode ?? this.themeMode,
      isClearingHistory: isClearingHistory ?? this.isClearingHistory,
    );
  }
}

class SettingsController extends StateNotifier<SettingsState> {
  final Ref _ref;

  SettingsController(this._ref) : super(const SettingsState());

  void setThemeMode(ThemeMode mode) {
    state = state.copyWith(themeMode: mode);
  }

  Future<void> clearHistory() async {
    state = state.copyWith(isClearingHistory: true);
    try {
      final historyRepo = _ref.read(scanHistoryRepositoryProvider);
      await historyRepo.clearHistory();
    } finally {
      state = state.copyWith(isClearingHistory: false);
    }
  }
}

final settingsControllerProvider =
    StateNotifierProvider<SettingsController, SettingsState>((ref) {
  return SettingsController(ref);
});

final themeModeProvider = Provider<ThemeMode>((ref) {
  return ref.watch(settingsControllerProvider).themeMode;
});
