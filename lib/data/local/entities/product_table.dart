import 'package:drift/drift.dart';

class ProductsTable extends Table {
  TextColumn get barcode => text()();
  TextColumn get name => text()();
  TextColumn get brand => text()();
  TextColumn get imageUrl => text().nullable()();
  TextColumn get category => text()();
  TextColumn get ingredientsText => text()();
  DateTimeColumn get createdAt => dateTime().withDefault(currentDateAndTime)();

  @override
  Set<Column> get primaryKey => {barcode};
}
