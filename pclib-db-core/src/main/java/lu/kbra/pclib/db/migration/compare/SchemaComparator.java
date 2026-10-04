package lu.kbra.pclib.db.migration.compare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.TableStructure;

public final class SchemaComparator {

	private SchemaComparator() {
	}

	public static SchemaDelta compare(final TableStructure oldStructure, final TableStructure newStructure) {
		final List<SchemaChange> changes = new ArrayList<>();

		SchemaComparator.compareTableName(oldStructure, newStructure, changes);
		SchemaComparator.compareColumns(oldStructure, newStructure, changes);

		return new SchemaDelta(changes);
	}

	private static void
			compareTableName(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {
		final String oldName = oldStructure.getStructureName().getName();
		final String newName = newStructure.getStructureName().getName();

		if (!Objects.equals(oldName, newName)) {
			changes.add(new TableNameChanged(oldName, newName));
		}
	}

	private static void
			compareColumns(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {
		final Map<String, ColumnData> oldColumns = SchemaComparator.indexColumns(oldStructure.getColumns());
		final Map<String, ColumnData> newColumns = SchemaComparator.indexColumns(newStructure.getColumns());

		final Set<String> columnNames = new LinkedHashSet<>(oldColumns.keySet());
		columnNames.addAll(newColumns.keySet());

		for (final String columnName : columnNames) {
			final ColumnData oldColumn = oldColumns.get(columnName);
			final ColumnData newColumn = newColumns.get(columnName);

			if (oldColumn == null) {
				changes.add(new ColumnAdded(newColumn));
				continue;
			}

			if (newColumn == null) {
				changes.add(new ColumnRemoved(oldColumn));
				continue;
			}

			SchemaComparator.compareColumn(oldColumn, newColumn, changes);
		}
	}

	private static Map<String, ColumnData> indexColumns(final ColumnData[] columns) {
		final Map<String, ColumnData> result = new LinkedHashMap<>();

		if (columns == null) {
			return result;
		}

		for (final ColumnData column : columns) {
			result.put(column.getLocalName(), column);
		}

		return result;
	}

	private static void compareColumn(final ColumnData oldColumn, final ColumnData newColumn, final List<SchemaChange> changes) {
		if (!Objects.equals(SchemaComparator.getTypeName(oldColumn), SchemaComparator.getTypeName(newColumn))) {
			changes.add(new ColumnTypeChanged(oldColumn, newColumn));
		}

		if (oldColumn.isNullable() != newColumn.isNullable()) {
			changes.add(new ColumnNullableChanged(oldColumn, newColumn));
		}
	}

	private static String getTypeName(final ColumnData column) {
		return column.getType().getEncodingType().getTypeName();
	}

}
