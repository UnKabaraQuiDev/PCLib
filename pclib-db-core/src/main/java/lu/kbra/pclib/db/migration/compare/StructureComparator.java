package lu.kbra.pclib.db.migration.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import lu.kbra.pclib.db.domain.table.IndexData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

public final class StructureComparator {

	private StructureComparator() {
	}

	public static SchemaDelta compare(final DatabaseStructure oldStructure, final DatabaseStructure newStructure) {
		final List<SchemaChange> changes = new ArrayList<>();

		StructureComparator.compareTables(oldStructure.getTableStructures(), newStructure.getTableStructures(), changes);

		return new SchemaDelta(oldStructure, newStructure, changes);
	}

	private static void
			compareTables(final Set<TableStructure> oldTables, final Set<TableStructure> newTables, final List<SchemaChange> changes) {
		final Map<String, TableStructure> oldTableMap = StructureComparator.indexTables(oldTables);
		final Map<String, TableStructure> newTableMap = StructureComparator.indexTables(newTables);

		final Set<String> matchedOldNames = new HashSet<>();
		final Set<String> matchedNewNames = new HashSet<>();

		for (final Map.Entry<String, TableStructure> entry : oldTableMap.entrySet()) {
			final String tableName = entry.getKey();
			final TableStructure oldTable = entry.getValue();
			final TableStructure newTable = newTableMap.get(tableName);

			if (newTable == null) {
				continue;
			}

			matchedOldNames.add(tableName);
			matchedNewNames.add(tableName);

			changes.addAll(StructureComparator.compare(oldTable, newTable).getChanges());
		}

		for (final Map.Entry<String, TableStructure> oldEntry : oldTableMap.entrySet()) {
			final String oldTableName = oldEntry.getKey();

			if (matchedOldNames.contains(oldTableName)) {
				continue;
			}

			final TableStructure oldTable = oldEntry.getValue();

			TableStructure renamedTable = null;
			String renamedTableName = null;

			for (final Map.Entry<String, TableStructure> newEntry : newTableMap.entrySet()) {
				final String newTableName = newEntry.getKey();

				if (matchedNewNames.contains(newTableName)) {
					continue;
				}

				final TableStructure newTable = newEntry.getValue();

				if (Objects.equals(oldTable.getStringHint(DefaultQueryableHints.TABLE_ID),
						newTable.getStringHint(DefaultQueryableHints.TABLE_ID)) && !Objects.equals(oldTableName, renamedTableName)) {
					renamedTable = newTable;
					renamedTableName = newTableName;
					break;
				}
			}

			if (renamedTable != null) {
				matchedOldNames.add(oldTableName);
				matchedNewNames.add(renamedTableName);

				changes.add(new TableNameChanged(oldTable.getNameParts(), renamedTable.getNameParts()));

				changes.addAll(StructureComparator.compare(oldTable, renamedTable).getChanges());
			}
		}

		for (final Map.Entry<String, TableStructure> entry : oldTableMap.entrySet()) {
			if (!matchedOldNames.contains(entry.getKey())) {
				changes.add(new TableRemoved(entry.getValue()));
			}
		}

		for (final Map.Entry<String, TableStructure> entry : newTableMap.entrySet()) {
			if (entry.getValue().isSynthetic() || entry.getValue().isInternal()) {
				continue;
			}
			if (!matchedNewNames.contains(entry.getKey())) {
				changes.add(new TableAdded(entry.getValue()));
			}
		}
	}

	private static Map<String, TableStructure> indexTables(final Set<TableStructure> tables) {
		final Map<String, TableStructure> result = new LinkedHashMap<>();

		if (tables == null) {
			return result;
		}
		for (final TableStructure table : tables) {
			if (table.isSynthetic() || table.isInternal()) {
				continue;
			}
			result.put(table.getStructureName().getName(), table);
		}

		return result;
	}

	public static SchemaDelta compare(final TableStructure oldStructure, final TableStructure newStructure) {
		return StructureComparator.compare(oldStructure, newStructure, false);
	}

	public static SchemaDelta compareRenamedTable(final TableStructure oldStructure, final TableStructure newStructure) {
		return StructureComparator.compare(oldStructure, newStructure, true);
	}

	private static SchemaDelta compare(final TableStructure oldStructure, final TableStructure newStructure, final boolean tableRenamed) {
		final List<SchemaChange> changes = new ArrayList<>();

		StructureComparator.compareColumns(oldStructure, newStructure, changes);
		StructureComparator.compareConstraints(oldStructure, newStructure, changes);

		return new SchemaDelta(null, null, changes);
	}

	private static void
			compareConstraints(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {
		final Map<String, ConstraintData> oldConstraints = StructureComparator.indexConstraints(oldStructure.getConstraints());
		final Map<String, ConstraintData> newConstraints = StructureComparator.indexConstraints(newStructure.getConstraints());
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
				changes.add(new ConstraintRemoved(oldStructure, oldConstraint));
				continue;
			}
			if (!StructureComparator.sameConstraint(oldConstraint, newConstraint)) {
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
			return StructureComparator.samePrimaryKey((PrimaryKeyData) oldConstraint, (PrimaryKeyData) newConstraint);
		}
		if (oldConstraint instanceof UniqueData) {
			return StructureComparator.sameUnique((UniqueData) oldConstraint, (UniqueData) newConstraint);
		}
		if (oldConstraint instanceof ForeignKeyData) {
			return StructureComparator.sameForeignKey((ForeignKeyData) oldConstraint, (ForeignKeyData) newConstraint);
		}
		if (oldConstraint instanceof CheckData) {
			return StructureComparator.sameCheck((CheckData) oldConstraint, (CheckData) newConstraint);
		}
		if (oldConstraint instanceof IndexData) {
			return StructureComparator.sameIndex((IndexData) oldConstraint, (IndexData) newConstraint);
		}
		throw new UnsupportedOperationException("Unknown Constraint type: " + oldConstraint.getClass().getName());
	}

	private static boolean sameCheck(final CheckData oldConstraint, final CheckData newConstraint) {
		return Objects.equals(oldConstraint.getExpression(), newConstraint.getExpression());
	}

	private static boolean samePrimaryKey(final PrimaryKeyData oldConstraint, final PrimaryKeyData newConstraint) {
		return Arrays.equals(StructureComparator.columnNames(oldConstraint.getColumns()),
				StructureComparator.columnNames(newConstraint.getColumns()));
	}

	private static boolean sameUnique(final UniqueData oldConstraint, final UniqueData newConstraint) {
		return Arrays.equals(StructureComparator.columnNames(oldConstraint.getColumns()),
				StructureComparator.columnNames(newConstraint.getColumns()));
	}

	private static boolean sameIndex(final IndexData oldConstraint, final IndexData newConstraint) {
		return Arrays.equals(StructureComparator.columnNames(oldConstraint.getColumns()),
				StructureComparator.columnNames(newConstraint.getColumns()));
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
			compareColumns(final TableStructure oldStructure, final TableStructure newStructure, final List<SchemaChange> changes) {

		final Map<String, ColumnData> oldColumns = StructureComparator.indexColumns(oldStructure.getColumns());
		final Map<String, ColumnData> newColumns = StructureComparator.indexColumns(newStructure.getColumns());

		final Set<String> columnNames = new LinkedHashSet<>(oldColumns.keySet());
		columnNames.addAll(newColumns.keySet());

		final List<ColumnData> removedColumns = new ArrayList<>();
		final List<ColumnData> addedColumns = new ArrayList<>();

		for (final String columnName : columnNames) {
			final ColumnData oldColumn = oldColumns.get(columnName);
			final ColumnData newColumn = newColumns.get(columnName);

			if (oldColumn == null) {
				addedColumns.add(newColumn);
				continue;
			}

			if (newColumn == null) {
				removedColumns.add(oldColumn);
				continue;
			}

			StructureComparator.compareColumn(newStructure, oldColumn, newColumn, changes);
		}

		for (final ColumnData oldColumn : removedColumns) {
			ColumnData renameCandidate = null;

			for (final ColumnData newColumn : addedColumns) {
				if (Objects.equals(StructureComparator.getTypeName(oldColumn), StructureComparator.getTypeName(newColumn))) {
					renameCandidate = newColumn;
					break;
				}
			}

			if (renameCandidate != null) {
				changes.add(new ColumnRenamed(newStructure, oldColumn, renameCandidate));
				addedColumns.remove(renameCandidate);
			} else {
				changes.add(new ColumnRemoved(newStructure, oldColumn));
			}
		}

		for (final ColumnData newColumn : addedColumns) {
			changes.add(new ColumnAdded(newStructure, newColumn));
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
		if (!Objects.equals(StructureComparator.getTypeName(oldColumn), StructureComparator.getTypeName(newColumn))) {
			changes.add(new ColumnTypeChanged(table, oldColumn, newColumn));
		}

		if (oldColumn.isNullable() != newColumn.isNullable()) {
			changes.add(new ColumnNullableChanged(table, oldColumn, newColumn));
		}
	}

	private static String getTypeName(final ColumnData column) {
		return column.getType().getEncodingType().build();
	}

}
