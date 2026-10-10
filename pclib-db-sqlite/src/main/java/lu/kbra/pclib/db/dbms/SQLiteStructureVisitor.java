package lu.kbra.pclib.db.dbms;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sqlite.jdbc4.JDBC4PreparedStatement;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.db.annotations.entry.ForeignKey.DeferMode;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.MigrationPhase;
import lu.kbra.pclib.db.migration.compare.ColumnAdded;
import lu.kbra.pclib.db.migration.compare.ColumnNullableChanged;
import lu.kbra.pclib.db.migration.compare.ColumnRemoved;
import lu.kbra.pclib.db.migration.compare.ColumnRenamed;
import lu.kbra.pclib.db.migration.compare.ColumnTypeChanged;
import lu.kbra.pclib.db.migration.compare.ConstraintAdded;
import lu.kbra.pclib.db.migration.compare.ConstraintChanged;
import lu.kbra.pclib.db.migration.compare.ConstraintRemoved;
import lu.kbra.pclib.db.migration.compare.SchemaChange;
import lu.kbra.pclib.db.migration.compare.SchemaDelta;
import lu.kbra.pclib.db.migration.compare.TableAdded;
import lu.kbra.pclib.db.migration.compare.TableNameChanged;
import lu.kbra.pclib.db.migration.compare.TableRemoved;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.transaction.DefaultTransactionOption;
import lu.kbra.pclib.db.transaction.TransactionIsolation;
import lu.kbra.pclib.db.transaction.TransactionOption;

import lombok.Data;

public class SQLiteStructureVisitor extends AbstractSQLStructureVisitor {

	public static final String CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY = SQLiteStructureVisitor.class.getSimpleName()
			+ ".clear_instead_of_truncate";
	public static boolean CLEAR_INSTEAD_OF_TRUNCATE = PCUtils.getBoolean(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY, false);

	public SQLiteStructureVisitor() {
		super.setCapability(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT, true);
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
		super.setCapability(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS, false);
		super.setCapability(DbmsCapability.SELECT_FOR_UPDATE_LOCKING, false);
		super.setCapability(DbmsCapability.WHERE_IN_TUPLES, false);
		super.setCapability(DbmsCapability.DEFERRABLE_FOREIGN_KEY, true);
	}

	@Override
	public Map<MigrationPhase, List<String>> migrate(final SchemaDelta delta) {
		if (delta == null || delta.isEmpty()) {
			return Collections.emptyMap();
		}

		final List<SchemaChange> changes = new ArrayList<>(delta.getChanges());
		final Map<String, Set<SchemaChange>> rebuilds = new LinkedHashMap<>();
		final Map<MigrationPhase, List<String>> result = new EnumMap<>(MigrationPhase.class);

		for (final SchemaChange change : changes) {
			if (this.requiresRebuild(change)) {
				final String tableId = this.getTableId(change);
//				if (tableId == null) {
//					throw new IllegalArgumentException("Change: " + change + " cannot be used to initiate a rebuild.");
//				}
				rebuilds.computeIfAbsent(tableId, k -> new HashSet<>()).add(change);
				continue;
			}

			final List<Pair<MigrationPhase, String[]>> sql = this.migrate(change);

			sql.forEach(pair -> {
				if (pair == null || !pair.hasValue() || pair.getValue().length == 0) {
					return;
				}

				Collections.addAll(result.computeIfAbsent(pair.getKey(), k -> new ArrayList<>()), pair.getValue());
			});
		}

//		for (final SchemaChange change : changes) {
//			final String tableId = this.getTableId(change);
//			if (tableId != null && rebuilds.containsKey(tableId)) {
//				rebuilds.get(tableId).add(change);
//			}
//		}

		for (final Entry<String, Set<SchemaChange>> entry : rebuilds.entrySet()) {
			final String tableId = entry.getKey();
			final Set<SchemaChange> tableChanges = entry.getValue();

			final TableStructure oldStructure = delta.getOldStructure()
					.getTableStructures()
					.stream()
					.filter(table -> Objects.equals(table.getTableId(), tableId))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Could not find old table for table ID: " + tableId));

			final TableStructure newStructure = delta.getNewStructure()
					.getTableStructures()
					.stream()
					.filter(table -> Objects.equals(table.getTableId(), tableId))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Could not find new table for table ID: " + tableId));

			final List<Pair<MigrationPhase, String[]>> sql = this.migrateRebuild(oldStructure, newStructure, tableChanges);

			sql.forEach(pair -> {
				if (pair == null || !pair.hasValue() || pair.getValue().length == 0) {
					return;
				}

				Collections.addAll(result.computeIfAbsent(pair.getKey(), k -> new ArrayList<>()), pair.getValue());
			});
		}

		result.values().forEach(list -> list.removeIf(sql -> sql == null || sql.trim().isEmpty()));

		return result;
	}

	private String getTableId(final SchemaChange change) {
		if (change instanceof ColumnRemoved) {
			return ((ColumnRemoved) change).getTableStructure().getTableId();
		}
		if (change instanceof ColumnTypeChanged) {
			return ((ColumnTypeChanged) change).getTableStructure().getTableId();
		}
		if (change instanceof ColumnNullableChanged) {
			return ((ColumnNullableChanged) change).getTableStructure().getTableId();
		}
		if (change instanceof ColumnAdded) {
			return ((ColumnAdded) change).getTableStructure().getTableId();
		}
		if (change instanceof ColumnRenamed) {
			return ((ColumnRenamed) change).getTableStructure().getTableId();
		}
		if (change instanceof ConstraintAdded) {
			return ((ConstraintAdded) change).getTable().getTableId();
		}
		if (change instanceof ConstraintRemoved) {
			return ((ConstraintRemoved) change).getTable().getTableId();
		}
		if (change instanceof ConstraintChanged) {
			return ((ConstraintChanged) change).getTable().getTableId();
		}
//		return null;

		throw new UnsupportedOperationException("Unsupported rebuild schema change: " + change.getClass().getName());
	}

	private List<Pair<MigrationPhase, String[]>>
			migrateRebuild(final TableStructure oldStructure, final TableStructure newStructure, final Set<SchemaChange> changes) {

		final List<Pair<MigrationPhase, String[]>> result = new ArrayList<>();
		for (SchemaChange c : changes) {
			if (c instanceof ColumnAdded) {
				final ColumnData cb = ((ColumnAdded) c).getColumn().deepClone();
				cb.getHints().put(DefaultColumnHints.NULLABLE, true);
				result.add(Pairs.readOnly(MigrationPhase.ADD_COLUMNS,
						this.migrate(new ColumnAdded(((ColumnAdded) c).getTableStructure(), cb))));
			} else if (c instanceof ColumnRenamed) {
				result.add(Pairs.readOnly(MigrationPhase.RENAME_COLUMS, this.migrate((ColumnRenamed) c)));
			}
		}

		final String[] newName = newStructure.getStructureName().getNameParts().clone();
		final String shortNewName = "_temp_" + newName[newName.length - 1] + "_new";
		newName[newName.length - 1] = shortNewName;

		final TableStructure tempStructure = new TableStructure(new StructureName(shortNewName, newName, this.qualifiedName(newName)),
				newStructure.getTargetClass(),
				newStructure.getEntryClass(),
				newStructure.getHints());

		tempStructure.setColumns(newStructure.getColumns());
		tempStructure.setConstraints(newStructure.getConstraints());
		tempStructure.setDependencies(newStructure.getDependencies());

		final List<ColumnMapping> mappings = this.getColumnMappings(oldStructure, newStructure, changes);

		if (mappings.isEmpty() && oldStructure.getColumns().length > 0) {
			throw new IllegalStateException("No columns can be copied while rebuilding table " + oldStructure.getQualifiedName());
		}

		final String insertColumns = mappings.stream()
				.map(mapping -> "\t" + mapping.getNewColumn().getLocalQualifiedName())
				.collect(Collectors.joining(",\n"));

		final String selectColumns = mappings.stream()
				.map(mapping -> "\t" + mapping.getOldColumn().getLocalQualifiedName())
				.collect(Collectors.joining(",\n"));

		final String insertSql = "INSERT INTO " + tempStructure.getQualifiedName() + " (\n" + insertColumns + "\n)\nSELECT\n"
				+ selectColumns + "\nFROM " + newStructure.getQualifiedName() + ";"; // should've already been renamed

		result.add(Pairs.readOnly(MigrationPhase.CHANGE_COLUMNS,
				PCUtils.combineArrays(new String[] { "PRAGMA foreign_keys = OFF;" },
						this.create(tempStructure),
						new String[] {
								insertSql,
								"DROP TABLE " + newStructure.getQualifiedName() + ";",
								"ALTER TABLE " + tempStructure.getQualifiedName() + " RENAME TO " + newStructure.getQualifiedName()
										+ ";" })));
		return result;
	}

	private List<ColumnMapping>
			getColumnMappings(final TableStructure oldStructure, final TableStructure newStructure, final Set<SchemaChange> changes) {
		final Map<String, ColumnData> oldColumns = Arrays.stream(oldStructure.getColumns())
				.collect(Collectors.toMap(ColumnData::getLocalName, Function.identity(), (a, b) -> a, LinkedHashMap::new));

		final List<ColumnMapping> result = new ArrayList<>();

		for (final ColumnData newColumn : newStructure.getColumns()) {
			final ColumnData oldColumn = oldColumns.get(newColumn.getLocalName());

			if (oldColumn == null) {
				continue;
			}

			result.add(new ColumnMapping(oldColumn, newColumn));
		}

		for (final SchemaChange change : changes) {
			if (!(change instanceof ColumnRenamed)) {
				continue;
			}

			final ColumnRenamed renamed = (ColumnRenamed) change;
			final ColumnData oldColumn = renamed.getOldColumn();
			final ColumnData newColumn = renamed.getNewColumn();

			final boolean alreadyMapped = result.stream().anyMatch(mapping -> Objects.equals(mapping.getNewColumn(), newColumn));

			if (!alreadyMapped) {
				result.add(new ColumnMapping(oldColumn, newColumn));
			}
		}

		return result;
	}

	@Data
	private static class ColumnMapping {
		final ColumnData oldColumn;
		final ColumnData newColumn;
	}

	protected boolean requiresRebuild(final SchemaChange change) {
		return change instanceof ColumnRemoved || change instanceof ColumnTypeChanged || change instanceof ColumnNullableChanged
				|| change instanceof ConstraintAdded || change instanceof ConstraintRemoved || change instanceof ConstraintChanged
				|| change instanceof ColumnAdded && !((ColumnAdded) change).getColumn().isNullable() || change instanceof ColumnRenamed;
	}

	@Override
	protected List<Pair<MigrationPhase, String[]>> migrate(final SchemaChange change) {
		if (change instanceof TableAdded) {
			return Arrays.asList(Pairs.readOnly(MigrationPhase.ADD_TABLE, this.migrate((TableAdded) change)));
		}
		if (change instanceof TableRemoved) {
			return Arrays.asList(Pairs.readOnly(MigrationPhase.REMOVE_TABLES, this.migrate((TableRemoved) change)));
		}
		if (change instanceof TableNameChanged) {
			return Arrays.asList(Pairs.readOnly(MigrationPhase.RENAME_TABLES, this.migrate((TableNameChanged) change)));
		}
		if (change instanceof ColumnAdded) {
			return Arrays.asList(Pairs.readOnly(MigrationPhase.ADD_COLUMNS, this.migrate((ColumnAdded) change)));
		}
		if (change instanceof ColumnRenamed) {
			return Arrays.asList(Pairs.readOnly(MigrationPhase.RENAME_COLUMS, this.migrate((ColumnRenamed) change)));
		}
		if (change instanceof ColumnRemoved) {
			throw new UnsupportedOperationException("ColumnRemoved must be handled by a table rebuild");
		}
		if (change instanceof ColumnTypeChanged) {
			throw new UnsupportedOperationException("ColumnTypeChanged must be handled by a table rebuild");
		}
		if (change instanceof ColumnNullableChanged) {
			throw new UnsupportedOperationException("ColumnNullableChanged must be handled by a table rebuild");
		}
		if (change instanceof ConstraintAdded) {
			throw new UnsupportedOperationException("ConstraintAdded must be handled by a table rebuild");
		}
		if (change instanceof ConstraintRemoved) {
			throw new UnsupportedOperationException("ConstraintRemoved must be handled by a table rebuild");
		}
		if (change instanceof ConstraintChanged) {
			throw new UnsupportedOperationException("ConstraintChanged must be handled by a table rebuild");
		}

		throw new UnsupportedOperationException("Unsupported schema change: " + change.getClass().getName());
	}

	@Override
	protected String[] migrate(final ConstraintRemoved change) {
		final ConstraintData constraint = change.getOldConstraint();

		if (constraint instanceof UniqueData) {
			return new String[] { "DROP INDEX " + this.qualifiedName(constraint.getName()) + ";" };
		}
		if (constraint instanceof ForeignKeyData) {
			throw new UnsupportedOperationException("SQLite cannot directly drop a foreign key. The table must be rebuilt.");
		}
		if (constraint instanceof CheckData) {
			throw new UnsupportedOperationException("SQLite cannot directly drop a CHECK constraint. The table must be rebuilt.");
		}
		if (constraint instanceof PrimaryKeyData) {
			throw new UnsupportedOperationException("SQLite cannot directly drop a primary key. The table must be rebuilt.");
		}

		throw new UnsupportedOperationException("Unsupported constraint type: " + constraint.getClass().getName());
	}

	@Override
	protected String buildDeferrableForeignKey(final DeferMode deferMode) {
		switch (deferMode) {
		case INITIALLY_IMMEDIATE:
			return "DEFERRABLE INITIALLY IMMEDIATE";
		case INITIALLY_DEFERRED:
			return "DEFERRABLE INITIALLY DEFERRED";
		case NOT_DEFERRABLE:
			return "NOT DEFERRABLE";
		default:
			throw new IllegalArgumentException(Objects.toString(deferMode));
		}
	}

	@Override
	protected void buildTransactionOption(final TransactionOption option, final Set<TransactionOption> options2, final List<String> lines) {
		if (option instanceof TransactionIsolation) {
			switch ((TransactionIsolation) option) {
			case READ_UNCOMMITTED:
				lines.add("PRAGMA read_uncommitted = ON;");
				break;

			case READ_COMMITTED:
			case REPEATABLE_READ:
			case SERIALIZABLE:
				lines.add("PRAGMA read_uncommitted = OFF;");
				break;
			}
			return;
		} else if (option instanceof DefaultTransactionOption) {
			switch ((DefaultTransactionOption) option) {
			case DEFER_FOREIGN_KEYS:
				lines.add("PRAGMA defer_foreign_keys = ON;");
				break;
			case IMMEDIATE_FOREIGN_KEYS:
				lines.add("PRAGMA defer_foreign_keys = OFF;");
				break;
			case READ_ONLY:
			default:
				break;
			}
			return;
		}

		super.buildTransactionOption(option, options2, lines);
	}

	@Override
	public String statementToString(final Statement stmt) {
		if (stmt instanceof JDBC4PreparedStatement) {
			return ((JDBC4PreparedStatement) stmt).toString();
		}

		return stmt.toString();
	}

	@Override
	public String create(final DatabaseStructure db) {
		throw new UnsupportedOperationException("SQLite does not support CREATE DATABASE.");
	}

	@Override
	protected String escapeEnd() {
		return "\"";
	}

	@Override
	protected String escapeStart() {
		return "\"";
	}

	@Override
	protected String joinKeyword(final Table.Type joinType) {
		if (joinType == Table.Type.RIGHT || joinType == Table.Type.FULL) {
			throw new UnsupportedOperationException("SQLite does not support " + joinType.name() + " JOIN.");
		}
		return super.joinKeyword(joinType);
	}

	@Override
	public <T extends DatabaseEntry> String getTruncateSQL(final AbstractDBTable<T> queryable) {
		if (super.getOptionOrDefault(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY,
				SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE)) {
			return "DELETE FROM " + queryable.getQualifiedName() + ";";
		}
		throw new UnsupportedOperationException("SQLite does not support TRUNCATE, use DELETE instead.");
	}

}
