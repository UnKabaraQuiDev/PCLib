package lu.kbra.pclib.db.dbms;

import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.postgresql.jdbc.PgStatement;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Tuples;
import lu.kbra.pclib.db.annotations.entry.ForeignKey.DeferMode;
import lu.kbra.pclib.db.autobuild.postgres.meta.PostgreSQLQueryableHints;
import lu.kbra.pclib.db.autobuild.postgres.meta.PostgreSQLViewHints;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.db.meta.DefaultDatabaseHints;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.IndexData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewCommonTableExpressionStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.MigrationPhase;
import lu.kbra.pclib.db.migration.compare.ColumnAdded;
import lu.kbra.pclib.db.migration.compare.ColumnNullableChanged;
import lu.kbra.pclib.db.migration.compare.ConstraintAdded;
import lu.kbra.pclib.db.migration.compare.ConstraintChanged;
import lu.kbra.pclib.db.migration.compare.ConstraintRemoved;
import lu.kbra.pclib.db.migration.compare.SchemaChange;
import lu.kbra.pclib.db.migration.compare.TableNameChanged;
import lu.kbra.pclib.db.transaction.TransactionIsolation;
import lu.kbra.pclib.db.transaction.TransactionOption;

public class PostgreSQLStructureVisitor extends AbstractSQLStructureVisitor {

	public PostgreSQLStructureVisitor() {
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
		super.setCapability(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS, true);
		super.setCapability(DbmsCapability.SELECT_FOR_UPDATE_LOCKING, true);
		super.setCapability(DbmsCapability.WHERE_IN_TUPLES, true);
		super.setCapability(DbmsCapability.DEFERRABLE_FOREIGN_KEY, true);
	}

	@Override
	protected String buildConstraint(final ConstraintData constraint) {
		if (constraint instanceof IndexData) {
			return null;
		}
		return super.buildConstraint(constraint);
	}

	@Override
	protected String[] migrateConstraintAdded(final ConstraintAdded change) {
		final TableStructure table = change.getTable();
		final ConstraintData constraint = change.getNewConstraint();

		if (constraint instanceof IndexData) {
			return new String[] {
					"CREATE INDEX " + this.qualifiedName(constraint.getName()) + " ON " + table.getQualifiedName() + " ("
							+ this.escapeList(((IndexData) constraint).getColumns()) + ");" };
		}

		return new String[] { "ALTER TABLE " + table.getQualifiedName() + " ADD " + this.buildConstraint(constraint) + ";" };
	}

	@Override
	protected String[] migrateConstraintChanged(final ConstraintChanged change) {
		final TableStructure table = change.getTable();
		final ConstraintData oldConstraint = change.getOldConstraint();
		final ConstraintData newConstraint = change.getNewConstraint();

		final String tableName = table.getQualifiedName();

		if (oldConstraint instanceof IndexData && newConstraint instanceof IndexData) {
			final IndexData oldIndex = (IndexData) oldConstraint;
			final IndexData newIndex = (IndexData) newConstraint;

			final String drop = "DROP INDEX " + this.qualifiedName(oldIndex.getName()) + ";";
			final String add = "CREATE INDEX " + this.qualifiedName(newIndex.getName()) + " ON " + tableName + " ("
					+ this.escapeList(newIndex.getColumns()) + ");";

			return new String[] { drop, add };
		}

		final String drop = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + this.qualifiedName(oldConstraint.getName()) + ";";
		final String add = "ALTER TABLE " + tableName + " ADD " + this.buildConstraint(newConstraint) + ";";

		return new String[] { drop, add };
	}

	@Override
	public String getDefaultSchema() {
		return PostgreSQLDbmsProvider.DEFAULT_SCHEMA;
	}

	@Override
	protected List<Pair<MigrationPhase, String[]>> migrateSchemaChange(final SchemaChange change) {
		if (change instanceof ColumnAdded) {
			final ColumnData cb = ((ColumnAdded) change).getColumn().deepClone();
			if (cb.isNullable()) {
				return Arrays.asList(Tuples.readOnly(MigrationPhase.ADD_COLUMNS,
						this.migrateColumnAdded(new ColumnAdded(((ColumnAdded) change).getTableStructure(), cb))));
			}
			cb.getHints().put(DefaultColumnHints.NULLABLE, true);
			return Arrays.asList(
					Tuples.readOnly(MigrationPhase.ADD_COLUMNS,
							this.migrateColumnAdded(new ColumnAdded(((ColumnAdded) change).getTableStructure(), cb))),
					Tuples.readOnly(MigrationPhase.CHANGE_COLUMNS,
							this.migrateColumnNullableChanged(new ColumnNullableChanged(((ColumnAdded) change).getTableStructure(),
									cb,
									((ColumnAdded) change).getColumn()))));
		}

		return super.migrateSchemaChange(change);
	}

	@Override
	protected String alterColumnType(final TableStructure table, final ColumnData name, final EncodingType<?> newType) {
		final String tableName = table.getQualifiedName();
		final String columnName = name.getLocalQualifiedName();

		return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE " + newType.build() + " USING "
				+ this.cast(columnName, newType) + ";";
	}

	@Override
	protected String[] migrateConstraintRemoved(final ConstraintRemoved change) {
		final TableStructure table = change.getTable();
		final ConstraintData constraint = change.getOldConstraint();
		final String tableName = table.getQualifiedName();
		final String constraintName = this.qualifiedName(constraint.getName());

		if (constraint instanceof IndexData) {
			return new String[] { "DROP INDEX " + constraintName + ";" };
		}

		if (constraint instanceof ForeignKeyData || constraint instanceof UniqueData || constraint instanceof CheckData
				|| constraint instanceof PrimaryKeyData) {
			return new String[] { "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";" };
		}

		throw new UnsupportedOperationException("Unsupported constraint type: " + constraint.getClass().getName());
	}

	@Override
	protected String[] migrateTableNameChanged(final TableNameChanged change) {
		final String[] newHalfName = change.getOldName().clone();
		newHalfName[newHalfName.length - 1] = change.getNewName()[change.getNewName().length - 1]; // <old>.<old>.<new>
		return change.getOldName()[change.getOldName().length - 2].equals(change.getNewName()[change.getNewName().length - 2])
				? new String[] { this.renameTable(change.getOldName(), change.getNewName()) }
				: new String[] {
						this.renameTable(change.getOldName(), change.getNewName()),
						this.moveTableSchema(newHalfName, change.getNewName()) };
	}

	protected String moveTableSchema(final String[] oldName, final String[] newSchema) {
		return "ALTER TABLE " + this.qualifiedName(oldName) + " SET SCHEMA " + this.qualifiedName(newSchema) + ";";
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
				lines.add("SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;");
				break;
			case READ_COMMITTED:
				lines.add("SET TRANSACTION ISOLATION LEVEL READ COMMITTED;");
				break;
			case REPEATABLE_READ:
				lines.add("SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;");
				break;
			case SERIALIZABLE:
				lines.add("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;");
				break;
			}
			return;
		}

		super.buildTransactionOption(option, options2, lines);
	}

	@Override
	public String statementToString(final Statement stmt) {
		if (stmt instanceof PgStatement) {
			return stmt.toString();
		}

		return stmt.toString();
	}

	@Override
	protected String cast(final EncodingType<?> encodingType) {
		return "CAST(? AS " + encodingType.cast() + ")";
	}

	protected String cast(final String column, final EncodingType<?> encodingType) {
		return "CAST(" + column + " AS " + encodingType.cast() + ")";
	}

	@Override
	public String[] create(final TableStructure table) {
		final String schema = this.schemaName(table);
		final StringBuilder sb = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ").append(this.qualifiedName(schema)).append(";");
		final String[] indices = Arrays.stream(table.getConstraints())
				.filter(IndexData.class::isInstance)
				.map(IndexData.class::cast)
				.map(c -> this.createIndex(table, c))
				.toArray(String[]::new);
		return PCUtils.combineArrays(new String[] { sb.toString() }, super.create(table), indices);
	}

	private String createIndex(final SQLQueryableStructure table, final IndexData c) {
		return "CREATE INDEX " + this.qualifiedName(c.getName()) + " ON " + table.getQualifiedName() + " ("
				+ this.escapeList(c.getColumns()) + ");";
	}

	@Override
	public String[] create(final ViewStructure view) {
		final String schema = this.schemaName(view);
		final StringBuilder sb = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ").append(this.qualifiedName(schema)).append(";");

		final StringBuilder sb2 = new StringBuilder();
		if (view.getCustomSQL() != null && !view.getCustomSQL().trim().isEmpty()) {
			return new String[] { view.getCustomSQL() };
		}
		final boolean materialized = view.getBooleanHint(PostgreSQLViewHints.MATERIALIZED, false);

		sb2.append("CREATE ");
		if (materialized) {
			sb.append("MATERIALIZED ");
		}
		sb2.append("VIEW ").append(view.getQualifiedName()).append(" AS \n");

		if (view.getWithTables().length != 0) {
			for (int i = 0; i < view.getWithTables().length; i++) {
				final ViewCommonTableExpressionStructure with = view.getWithTables()[i];
				sb2.append(i == 0 ? "WITH " : ", ");
				sb2.append(with.getName()).append(" AS (\n").append(this.buildWithSQL(with)).append("\n)\n");
			}
		}

		sb2.append(this.buildSelectBody(view)).append(";");

		if (materialized) {
			final String[] indices = Arrays.stream(view.getConstraints())
					.filter(IndexData.class::isInstance)
					.map(IndexData.class::cast)
					.map(c -> this.createIndex(view, c))
					.toArray(String[]::new);
			return PCUtils.combineArrays(new String[] { sb.toString(), this.drop(view), sb2.toString() }, indices);
		} else {
			return new String[] { sb.toString(), this.drop(view), sb2.toString() };
		}
	}

	@Override
	public String create(final DatabaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasHint(DefaultQueryableHints.CHARACTER_SET)) {
			final String encoding = db.getStringHint(DefaultDatabaseHints.CHARACTER_SET);
			sb.append(" ENCODING ").append(this.qualifiedName(encoding));
		}
		if (db.hasHint(PostgreSQLQueryableHints.LC_COLLATE)) {
			final String lcCollate = db.getStringHint(PostgreSQLQueryableHints.LC_COLLATE);
			sb.append(" LC_COLLATE ").append(this.qualifiedName(lcCollate));
		}
		if (db.hasHint(PostgreSQLQueryableHints.LC_CTYPE)) {
			final String lcCType = db.getStringHint(PostgreSQLQueryableHints.LC_CTYPE);
			sb.append(" LC_CTYPE ").append(this.qualifiedName(lcCType));
		}

		sb.append(';');
		return sb.toString();
	}

	@Override
	public String[] getQueryableNameParts(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		return new String[] { this.getSchemaName(queryableHints), this.getQueryableName(tableClass, queryableHints) };
	}

	public String getSchemaName(final Map<String, Object> hints) {
		return (String) hints.getOrDefault(DefaultQueryableHints.SCHEMA, this.getDefaultSchema());
	}

	public String schemaName(final TableStructure table) {
		return (String) table.getHints().getOrDefault(DefaultQueryableHints.SCHEMA, this.getDefaultSchema());
	}

	public String schemaName(final ViewStructure table) {
		return (String) table.getHints().getOrDefault(DefaultQueryableHints.SCHEMA, this.getDefaultSchema());
	}

	@Override
	protected String buildColumn(final TableStructure table, final ColumnData column, final boolean inlinePrimaryKey) {
		if (column.isGenerated()) {
			return this.buildGeneratedColumn(column);
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(this.qualifiedName(column.getLocalName())).append(" ");
		if (column.isAutoIncrement()) {
			sb.append(this.serialType(column));
		} else {
			sb.append(column.getType().getEncodingType().build());
		}

		if (!column.isNullable()) {
			sb.append(" NOT NULL");
		}

		if (column.hasDefaultValue()) {
			sb.append(" DEFAULT ").append(column.getStringHint(DefaultColumnHints.DEFAULT_VALUE));
		}

		return sb.toString();
	}

	@Override
	protected String buildGeneratedColumn(final ColumnData column) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.qualifiedName(column.getLocalName())).append(" ").append(column.getType().getEncodingType().build());
		sb.append(" GENERATED ALWAYS AS (").append(column.getStringHint(DefaultColumnHints.GENERATED_VALUE)).append(") STORED");
		return sb.toString();
	}

	@Override
	protected String escapeEnd() {
		return "\"";
	}

	@Override
	protected String escapeStart() {
		return "\"";
	}

	private String serialType(final ColumnData column) {
		switch (column.getType().getEncodingType().getSQLType()) {
		case Types.BIGINT:
			return "BIGSERIAL";
		case Types.SMALLINT:
			return "SMALLSERIAL";
		default:
			return "SERIAL";
		}
	}

}
