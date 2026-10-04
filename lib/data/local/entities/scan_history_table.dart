import 'package:drift/drift.dart';

class ScanHistoryTable extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get barcode => text()();
  DateTimeColumn get scannedAt => dateTime().withDefault(currentDateAndTime)();
}
