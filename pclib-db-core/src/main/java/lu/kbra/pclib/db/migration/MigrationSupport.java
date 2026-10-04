package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lu.kbra.pclib.db.annotations.entry.ForeignKey.DeferMode;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;
import lu.kbra.pclib.db.migration.compare.SchemaComparator;
import lu.kbra.pclib.db.migration.compare.SchemaDelta;
import lu.kbra.pclib.db.migration.schema.ConstraintType;
import lu.kbra.pclib.db.migration.schema.data.MigrationColumnData;
import lu.kbra.pclib.db.migration.schema.data.MigrationConstraintData;
import lu.kbra.pclib.db.migration.schema.data.MigrationData;
import lu.kbra.pclib.db.migration.schema.data.MigrationHistoryData;
import lu.kbra.pclib.db.migration.schema.data.MigrationTableData;
import lu.kbra.pclib.db.migration.schema.table.MigrationColumnTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationConstraintTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationHistoryTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationTableTable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationSupport {

	private Database database;
	private DatabaseEntryUtils dbEntryUtils;

	private MigrationTable migrationTable;
	private MigrationTableTable migrationTableTable;
	private MigrationColumnTable migrationColumnTable;
	private MigrationConstraintTable migrationConstraintTable;
	private MigrationHistoryTable migrationHistoryTable;

	private String applicationVersion;

	public MigrationSupport(final Database db, final String migrationName) {
		this.database = db;
		this.dbEntryUtils = db.getDatabaseEntryUtils();

		this.migrationTable = new MigrationTable(db, migrationName);
		this.migrationTableTable = new MigrationTableTable(db, migrationName);
		this.migrationColumnTable = new MigrationColumnTable(db, migrationName);
		this.migrationConstraintTable = new MigrationConstraintTable(db, migrationName);
		this.migrationHistoryTable = new MigrationHistoryTable(db, migrationName);
	}

	public SQLQueryable<?>[] getTables() {
		return new SQLQueryable<?>[] {
				this.migrationTable,
				this.migrationTableTable,
				this.migrationColumnTable,
				this.migrationConstraintTable,
				this.migrationHistoryTable };
	}

	/**
	 * @return {@code true} if changes were made
	 */
	public boolean migrate(final List<? extends DatabaseMigration> migrations) {
		this.applicationVersion = this.database.getStructure().getStringHint(DefaultQueryableHints.APPLICATION_VERSION, null);

//		final long started = System.currentTimeMillis();

		try {
			this.ensureInitialSnapshot();

			this.migrateAutomatic();

			this.runManualMigrations(migrations);
		} catch (final Throwable e) {
			throw new InternalDBException("Error executing database migration.", null, this.database.getStructure(), e);
		}

		return false;
	}

	private void ensureInitialSnapshot() throws DBException {
		final Optional<MigrationData> latest = this.migrationTable.findLatestMigration();

		if (latest.isPresent()) {
			return;
		}

		final DatabaseStructure structure = database.getStructure();

		final String schemaHash = SchemaHashCalculator.calculate(structure);

		final MigrationData migration = new MigrationData();

		migration.setVersion(1);
		migration.setSchemaHash(schemaHash);
		migration.setAppliedAt(new Timestamp(System.currentTimeMillis()));
		migration.setType(MigrationType.INITIAL);
		migration.setApplicationVersion(this.applicationVersion);
		migration.setDescription("Initial database schema");
		migration.setExecutionTimeMs(Duration.ofMillis(0));

		this.storeSnapshot(migration, structure);
	}

	private void runManualMigrations(final List<? extends DatabaseMigration> migrations) throws DBException {
		if (migrations == null || migrations.isEmpty()) {
			return;
		}

		final List<DatabaseMigration> sorted = new ArrayList<>(migrations);

		sorted.sort(Comparator.comparing(DatabaseMigration::order));

		for (final DatabaseMigration migration : sorted) {
			if (this.migrationHistoryTable.findAppliedMigration(migration.id()).isPresent()) {
				continue;
			}

			this.executeManualMigration(database, migration);
		}
	}

	private void executeManualMigration(final Database database, final DatabaseMigration migration) throws DBException {
		final long started = System.currentTimeMillis();

		final Optional<MigrationData> before = this.migrationTable.findLatestMigration();

		final String beforeHash = before.isPresent() ? before.get().getSchemaHash() : null;

		try (AbstractConnection c = database.use()) {
			migration.up(c);

			final long executionTime = System.currentTimeMillis() - started;

			database.scanFromBeans();

			final String afterHash = SchemaHashCalculator.calculate(database.getStructure());

			final MigrationHistoryData history = new MigrationHistoryData();

			history.setMigrationId(migration.id());
			history.setOrder(migration.order());
			history.setName(migration.name());
			history.setBeforeSchemaHash(beforeHash);
			history.setAfterSchemaHash(afterHash);
			history.setAppliedAt(new Timestamp(System.currentTimeMillis()));
			history.setExecutionTimeMs(Duration.ofMillis(executionTime));
			history.setApplicationVersion(this.applicationVersion);
			history.setDescription(migration.description());

			this.migrationHistoryTable.insert(history);
		} catch (final DBException e) {
			throw e;
		} catch (final Exception e) {
			throw new DBException("Migration failed: " + migration.id(), e);
		}
	}

	private void migrateAutomatic() throws DBException {
		final Optional<MigrationData> previous = this.migrationTable.findLatestMigration();

		if (!previous.isPresent()) {
			throw new DBException("Migration system has no initial schema snapshot.");
		}

		final DatabaseStructure current = database.getStructure();
		final DatabaseStructure previousStructure = this.loadSnapshot(previous.get());

		final SchemaDelta delta = SchemaComparator.compare(previousStructure, current);

		if (delta.isEmpty()) {
			return;
		}

		final long started = System.currentTimeMillis();

		final List<String> sql = dbEntryUtils.getStructureVisitor().migrate(delta);

		try (AbstractConnection c = database.use(); Statement stmt = c.createStatement()) {
			c.setAutoCommit(false);
			try {
				for (String s : sql) {
					System.out.println("Executing: " + s);
					try {
						stmt.execute(s);
					} catch (SQLException e) {
						throw new InternalDBException(null, s, null, e);
					}
				}
				c.commit();
			} finally {
				c.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new InternalDBException(e);
		}

		final int newVersion = previous.get().getVersion() + 1;

		final String schemaHash = SchemaHashCalculator.calculate(current);

		final MigrationData migration = new MigrationData();

		migration.setVersion(newVersion);
		migration.setSchemaHash(schemaHash);
		migration.setAppliedAt(new Timestamp(System.currentTimeMillis()));
		migration.setType(MigrationType.AUTOMATIC);
		migration.setApplicationVersion(this.applicationVersion);
		migration.setDescription("Automatic schema migration");
		migration.setExecutionTimeMs(Duration.ofMillis(System.currentTimeMillis() - started));

		this.storeSnapshot(migration, current);
	}

	private void storeSnapshot(final MigrationData migration, final DatabaseStructure structure) throws DBException {
		this.migrationTable.insert(migration);

		for (final TableStructure table : structure.getTableStructures()) {
			final MigrationTableData migrationTable = new MigrationTableData();

			migrationTable.setMigrationId(migration.getId());
			migrationTable.setName(table.getStructureName().getName());
			migrationTable.setQualifiedName(table.getStructureName().getQualifiedName().toString());

			migrationTable.setStructureName(table.getStructureName().getName());

			this.migrationTableTable.insert(migrationTable);

			final ColumnData[] columns = table.getColumns();

			if (columns != null) {
				for (final ColumnData column : columns) {
					final MigrationColumnData migrationColumn = new MigrationColumnData();

					migrationColumn.setTableId(migrationTable.getId());
					migrationColumn.setName(column.getLocalName());
					migrationColumn.setQualifiedName(column.getLocalQualifiedName());
					migrationColumn.setTypeName(column.getType().getEncodingType().getTypeName());
					migrationColumn.setNullable(column.isNullable());
					migrationColumn.setPrimaryKey(column.isPrimaryKey());
					migrationColumn.setUnique(column.isUnique());
					migrationColumn.setAutoIncrement(column.isAutoIncrement());
					migrationColumn.setDefaultValue(column.getDefaultValue());

					this.migrationColumnTable.insert(migrationColumn);
				}
			}

			final ConstraintData[] constraints = table.getConstraints();

			if (constraints != null) {
				for (final ConstraintData constraint : constraints) {
					final MigrationConstraintData migrationConstraint = new MigrationConstraintData();

					migrationConstraint.setTableId(migrationTable.getId());
					migrationConstraint.setName(constraint.getName());
					final ConstraintType type;
					final String def;
					if (constraint instanceof UniqueData) {
						type = ConstraintType.UNIQUE;
						def = serializeUnique((UniqueData) constraint);
					} else if (constraint instanceof ForeignKeyData) {
						type = ConstraintType.FOREIGN_KEY;
						def = serializeForeignKey((ForeignKeyData) constraint);
					} else if (constraint instanceof PrimaryKeyData) {
						type = ConstraintType.PRIMARY_KEY;
						def = serializePrimaryKey((PrimaryKeyData) constraint);
					} else if (constraint instanceof CheckData) {
						type = ConstraintType.CHECK;
						def = serializeCheck((CheckData) constraint);
					} else {
						throw new IllegalArgumentException("Unknown constraint type: " + constraint.getClass().getName());
					}
					migrationConstraint.setType(type);
					migrationConstraint.setDefinition(def);

					this.migrationConstraintTable.insert(migrationConstraint);
				}
			}
		}
	}

	private DatabaseStructure loadSnapshot(final MigrationData previous) throws DBException {
		final DatabaseStructure structure = new DatabaseStructure();

		final List<MigrationTableData> tables = this.migrationTableTable.findByMigrationId(previous.getId());

		for (final MigrationTableData migrationTable : tables) {
			final StructureName structureName = new StructureName(migrationTable.getName(),
					this.dbEntryUtils.getStructureVisitor().unqualifyName(migrationTable.getQualifiedName()),
					migrationTable.getQualifiedName());

			final TableStructure tableStructure = new TableStructure(structureName, null, null, Collections.<String, Object>emptyMap());

			final List<MigrationColumnData> columns = this.migrationColumnTable.findByTableId(migrationTable.getId());
			final ColumnData[] columnData = new ColumnData[columns.size()];
			for (int i = 0; i < columns.size(); i++) {
				columnData[i] = deserializeColumnData(columns.get(i));
			}
			tableStructure.setColumns(columnData);

			{
				final List<MigrationConstraintData> constraints = this.migrationConstraintTable.findByTableId(migrationTable.getId());
				final ConstraintData[] constraintData = new ConstraintData[constraints.size()];
				for (int i = 0; i < constraints.size(); i++) {
					final MigrationConstraintData v = constraints.get(i);
					final ConstraintData cd;
					switch (v.getType()) {
					case CHECK:
						cd = new CheckData(tableStructure, v.getDefinition());
						break;
					case FOREIGN_KEY:
						cd = deserializeForeignKey(v.getDefinition());
						break;
					case PRIMARY_KEY:
						cd = deserializePrimaryKey(v.getDefinition());
						break;
					case UNIQUE:
						cd = deserializeUnique(v.getDefinition());
						break;
					default:
						throw new IllegalArgumentException("Unknown constraint type: " + v.getType());
					}
					constraintData[i] = cd;
				}
				tableStructure.setConstraints(constraintData);
			}

			tableStructure.setDependencies(Collections.<SQLQueryableDependency>emptySet());

			structure.getTableStructures().add(tableStructure);
		}

		return structure;
	}

	private static ColumnData deserializeColumnData(MigrationColumnData migrationColumnData) {
		final Map<String, Object> hints = new HashMap<>();
		hints.put(DefaultColumnHints.AUTO_INCREMENT, migrationColumnData.isAutoIncrement());
		hints.put(DefaultColumnHints.PRIMARY_KEY, migrationColumnData.isPrimaryKey());
		hints.put(DefaultColumnHints.UNIQUE, migrationColumnData.isUnique());
		hints.put(DefaultColumnHints.NULLABLE, migrationColumnData.isNullable());
		hints.put(DefaultColumnHints.DEFAULT_VALUE, migrationColumnData.getDefaultValue());
		return new ColumnData(migrationColumnData.getName(),
				migrationColumnData.getQualifiedName(),
				null,
				null,
				new MigrationColumnType(migrationColumnData.getTypeName()),
				null,
				hints);
	}

	public static String serializeForeignKey(final ForeignKeyData data) {
		return String.join("\n",
				MigrationSupport.escape(data.getName()),
				MigrationSupport.serializeArray(data.getColumns()),
				MigrationSupport.serializeArray(data.getReferencedColumns()),
				MigrationSupport.serializeStructureName(data.getResolvedName()),
				MigrationSupport.serializeEnum(data.getOnDeleteAction()),
				MigrationSupport.serializeEnum(data.getOnUpdateAction()),
				MigrationSupport.serializeEnum(data.getDeferMode()));
	}

	public static ForeignKeyData deserializeForeignKey(final String serialized) {
		final String[] lines = serialized.split("\n", -1);

		if (lines.length != 7) {
			throw new IllegalArgumentException(
					"Invalid serialized ConstraintData: expected 7 lines, got " + lines.length + "\n:" + serialized);
		}

		return new ForeignKeyData(MigrationSupport.unescape(lines[0]),
				MigrationSupport.deserializeArray(lines[1]),
				MigrationSupport.deserializeArray(lines[2]),
				null,
				MigrationSupport.deserializeStructureName(lines[3]),
				MigrationSupport.deserializeEnum(lines[4], OnAction.class),
				MigrationSupport.deserializeEnum(lines[5], OnAction.class),
				MigrationSupport.deserializeEnum(lines[6], DeferMode.class));
	}

	public static String serializeUnique(final UniqueData data) {
		return String.join("\n", MigrationSupport.escape(data.getName()), MigrationSupport.serializeColumnArray(data.getColumns()));
	}

	public static UniqueData deserializeUnique(final String serialized) {
		final String[] lines = serialized.split("\n", -1);

		if (lines.length != 2) {
			throw new IllegalArgumentException("Invalid serialized UniqueData: expected 2 lines, got " + lines.length + "\n:" + serialized);
		}

		return new UniqueData(MigrationSupport.unescape(lines[0]), deserializeColumnArray(lines[1]));
	}

	public static String serializePrimaryKey(final PrimaryKeyData data) {
		return String.join("\n", MigrationSupport.escape(data.getName()), MigrationSupport.serializeColumnArray(data.getColumns()));
	}

	public static PrimaryKeyData deserializePrimaryKey(final String serialized) {
		final String[] lines = serialized.split("\n", -1);

		if (lines.length != 2) {
			throw new IllegalArgumentException(
					"Invalid serialized PrimaryKeyData: expected 2 lines, got " + lines.length + "\n:" + serialized);
		}

		return new PrimaryKeyData(MigrationSupport.unescape(lines[0]), deserializeColumnArray(lines[1]));
	}

	public static String serializeCheck(CheckData data) {
		return String.join("\n", MigrationSupport.escape(data.getName()), MigrationSupport.escape(data.getExpression()));
	}

	public static CheckData deserializeCheck(String serialized) {
		final String[] lines = serialized.split("\n", -1);

		if (lines.length != 2) {
			throw new IllegalArgumentException("Invalid serialized CheckData: expected 2 lines, got " + lines.length + "\n:" + serialized);
		}

		return new CheckData(MigrationSupport.unescape(lines[0]), MigrationSupport.unescape(lines[1]));
	}

	private static String serializeColumnArray(final ColumnData[] columns) {
		if (columns == null) {
			return "";
		}

		final StringBuilder result = new StringBuilder();

		for (int i = 0; i < columns.length; i++) {
			if (i > 0) {
				result.append(",");
			}

			result.append(columns[i].getLocalName());
		}

		return result.toString();
	}

	private static ColumnData[] deserializeColumnArray(final String value) {
		if (value == null || value.isEmpty()) {
			return new ColumnData[0];
		}

		final String[] values = value.split(",", -1);
		final ColumnData[] result = new ColumnData[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = new ColumnData(values[i], null, null, null, null, null, null);
		}

		return result;
	}

	private static String unescape(final String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		final StringBuilder result = new StringBuilder();

		boolean escaped = false;

		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);

			if (escaped) {
				switch (c) {
				case 'n':
					result.append('\n');
					break;
				case 'r':
					result.append('\r');
					break;
				case '\\':
					result.append('\\');
					break;
				default:
					result.append('\\').append(c);
					break;
				}

				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			} else {
				result.append(c);
			}
		}

		if (escaped) {
			result.append('\\');
		}

		return result.toString();
	}

	private static String serializeArray(final String[] values) {
		if (values == null) {
			return "";
		}

		final StringBuilder result = new StringBuilder();

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				result.append(',');
			}

			result.append(MigrationSupport.escape(values[i]));
		}

		return result.toString();
	}

	private static String[] deserializeArray(final String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		final String[] parts = value.split(",", -1);

		for (int i = 0; i < parts.length; i++) {
			parts[i] = MigrationSupport.unescape(parts[i]);
		}

		return parts;
	}

	private static String escape(final String value) {
		if (value == null) {
			return "";
		}

		return value.replace("\\", "\\\\").replace(",", "\\,").replace("\n", "\\n").replace("\r", "\\r");
	}

	private static <E extends Enum<E>> String serializeEnum(final E value) {
		return value == null ? "" : value.name();
	}

	private static <E extends Enum<E>> E deserializeEnum(final String value, final Class<E> enumClass) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		return Enum.valueOf(enumClass, value);
	}

	private static String serializeStructureName(final StructureName value) {
		if (value == null) {
			return "";
		}

		return MigrationSupport.escape(value.getName()) + "|" + MigrationSupport.serializeArray(value.getNameParts()) + "|"
				+ MigrationSupport.escape(value.getQualifiedName().toString());
	}

	private static StructureName deserializeStructureName(final String serialized) {
		if (serialized == null || serialized.isEmpty()) {
			return null;
		}

		final String[] parts = serialized.split("\\|", -1);

		if (parts.length != 3) {
			throw new IllegalArgumentException(
					"Invalid serialized StructureName: expected 3 parts, got " + parts.length + "\n:" + serialized);
		}

		final String name = MigrationSupport.unescape(parts[0]);
		final String[] nameParts = MigrationSupport.deserializeArray(parts[1]);
		final String qualifiedName = MigrationSupport.unescape(parts[2]);

		return new StructureName(name, nameParts, qualifiedName);
	}

}
