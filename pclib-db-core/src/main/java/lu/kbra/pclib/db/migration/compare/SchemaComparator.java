package lu.kbra.pclib.db.migration.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;

public final class SchemaComparator {

	private SchemaComparator() {
	}

	public static SchemaDelta compare(final DatabaseStructure oldStructure, final DatabaseStructure newStructure) {
		final List<SchemaChange> changes = new ArrayList<>();

		SchemaComparator.compareTables(oldStructure.getTableStructures(), newStructure.getTableStructures(), changes);

		return new SchemaDelta(changes);
	}

	private static void
			compareTables(final Set<TableStructure> oldTables, final Set<TableStructure> newTables, final List<SchemaChange> changes) {
		final Map<String, TableStructure> oldTableMap = SchemaComparator.indexTables(oldTables);
		final Map<String, TableStructure> newTableMap = SchemaComparator.indexTables(newTables);

		final Set<String> tableNames = new LinkedHashSet<>(oldTableMap.keySet());

		tableNames.addAll(newTableMap.keySet());

		for (final String tableName : tableNames) {
			final TableStructure oldTable = oldTableMap.get(tableName);
			final TableStructure newTable = newTableMap.get(tableName);

			if (oldTable == null) {
				changes.add(new TableAdded(newTable));
				continue;
			}
			if (newTable == null) {
				changes.add(new TableRemoved(oldTable));
				continue;
			}

			changes.addAll(SchemaComparator.compare(oldTable, newTable).getChanges());
		}
	}

	private static Map<String, TableStructure> indexTables(final Set<TableStructure> tables) {
		final Map<String, TableStructure> result = new LinkedHashMap<>();

		if (tables == null) {
			return result;
		}
		for (final TableStructure table : tables) {
			result.put(table.getStructureName().getName(), table);
		}

		return result;
	}

	public static SchemaDelta compare(final TableStructure oldStructure, final TableStructure newStructure) {
		final List<SchemaChange> changes = new ArrayList<>();

		SchemaComparator.compareTableName(oldStructure, newStructure, changes);
		SchemaComparator.compareColumns(oldStructure, newStructure, changes);
		SchemaComparator.compareConstraints(oldStructure, newStructure, changes);

		return new SchemaDelta(changes);
	}

	private static void
			compareConstraints(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {
		final Map<String, ConstraintData> oldConstraints = SchemaComparator.indexConstraints(oldStructure.getConstraints());
		final Map<String, ConstraintData> newConstraints = SchemaComparator.indexConstraints(newStructure.getConstraints());
		final Set<String> constraintNames = new LinkedHashSet<>(oldConstraints.keySet());

		constraintNames.addAll(newConstraints.keySet());

		for (final String constraintName : constraintNames) {
			final ConstraintData oldConstraint = oldConstraints.get(constraintName);
			final ConstraintData newConstraint = newConstraints.get(constraintName);

			if (oldConstraint == null) {
				changes.add(new ConstraintAdded(newStructure, newConstraint));
				continue;
			}
			if (newConstraint == null) {
				changes.add(new ConstraintRemoved(newStructure, oldConstraint));
				continue;
			}
			if (!SchemaComparator.sameConstraint(oldConstraint, newConstraint)) {
				changes.add(new ConstraintChanged(newStructure, oldConstraint, newConstraint));
			}
		}
	}

	private static Map<String, ConstraintData> indexConstraints(final ConstraintData[] constraints) {
		final Map<String, ConstraintData> result = new LinkedHashMap<>();

		if (constraints == null) {
			return result;
		}

		for (final ConstraintData constraint : constraints) {
			result.put(constraint.getName(), constraint);
		}

		return result;
	}

	private static boolean sameConstraint(final ConstraintData oldConstraint, final ConstraintData newConstraint) {
		if (!Objects.equals(oldConstraint.getClass(), newConstraint.getClass())) {
			return false;
		}
		if (oldConstraint instanceof PrimaryKeyData) {
			return SchemaComparator.samePrimaryKey((PrimaryKeyData) oldConstraint, (PrimaryKeyData) newConstraint);
		}
		if (oldConstraint instanceof UniqueData) {
			return SchemaComparator.sameUnique((UniqueData) oldConstraint, (UniqueData) newConstraint);
		}
		if (oldConstraint instanceof ForeignKeyData) {
			return SchemaComparator.sameForeignKey((ForeignKeyData) oldConstraint, (ForeignKeyData) newConstraint);
		}
		if (oldConstraint instanceof CheckData) {
			return SchemaComparator.sameCheck((CheckData) oldConstraint, (CheckData) newConstraint);
		}
		throw new UnsupportedOperationException("Unknown Constraint type: " + oldConstraint.getClass().getName());
	}

	private static boolean sameCheck(CheckData oldConstraint, CheckData newConstraint) {
		return Objects.equals(oldConstraint.getExpression(), newConstraint.getExpression());
	}

	private static boolean samePrimaryKey(final PrimaryKeyData oldConstraint, final PrimaryKeyData newConstraint) {
		return Arrays.equals(SchemaComparator.columnNames(oldConstraint.getColumns()),
				SchemaComparator.columnNames(newConstraint.getColumns()));
	}

	private static boolean sameUnique(final UniqueData oldConstraint, final UniqueData newConstraint) {
		return Arrays.equals(SchemaComparator.columnNames(oldConstraint.getColumns()),
				SchemaComparator.columnNames(newConstraint.getColumns()));
	}

	private static boolean sameForeignKey(final ForeignKeyData oldConstraint, final ForeignKeyData newConstraint) {
		return Arrays.equals(oldConstraint.getColumns(), newConstraint.getColumns())
				&& Arrays.equals(oldConstraint.getReferencedColumns(), newConstraint.getReferencedColumns())
				&& Objects.equals(oldConstraint.getResolvedName(), newConstraint.getResolvedName())
				&& Objects.equals(oldConstraint.getOnDeleteAction(), newConstraint.getOnDeleteAction())
				&& Objects.equals(oldConstraint.getOnUpdateAction(), newConstraint.getOnUpdateAction())
				&& Objects.equals(oldConstraint.getDeferMode(), newConstraint.getDeferMode());
	}

	private static String[] columnNames(final ColumnData[] columns) {
		if (columns == null) {
			return null;
		}

		final String[] result = new String[columns.length];

		for (int i = 0; i < columns.length; i++) {
			result[i] = columns[i].getLocalName();
		}

		return result;
	}

	private static void
			compareTableName(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {
		final String[] oldName = oldStructure.getStructureName().getNameParts();
		final String[] newName = newStructure.getStructureName().getNameParts();

		if (!Arrays.equals(oldName, newName)) {
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
				changes.add(new ColumnAdded(newStructure, newColumn));
				continue;
			}

			if (newColumn == null) {
				changes.add(new ColumnRemoved(newStructure, oldColumn));
				continue;
			}

			SchemaComparator.compareColumn(newStructure, oldColumn, newColumn, changes);
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

	private static void compareColumn(
			final TableStructure table,
			final ColumnData oldColumn,
			final ColumnData newColumn,
			final List<SchemaChange> changes) {
		if (!Objects.equals(SchemaComparator.getTypeName(oldColumn), SchemaComparator.getTypeName(newColumn))) {
			changes.add(new ColumnTypeChanged(table, oldColumn, newColumn));
		}

		if (oldColumn.isNullable() != newColumn.isNullable()) {
			changes.add(new ColumnNullableChanged(table, oldColumn, newColumn));
		}
	}

	private static String getTypeName(final ColumnData column) {
		return column.getType().getEncodingType().getTypeName();
	}

}
