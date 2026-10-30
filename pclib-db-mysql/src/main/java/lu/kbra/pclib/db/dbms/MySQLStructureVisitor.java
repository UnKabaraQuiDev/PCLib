package lu.kbra.pclib.db.dbms;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.mysql.cj.PreparedQuery;
import com.mysql.cj.jdbc.ClientPreparedStatement;

import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Tuples;
import lu.kbra.pclib.db.annotations.entry.ForeignKey.DeferMode;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.MigrationPhase;
import lu.kbra.pclib.db.migration.compare.ColumnRenamed;
import lu.kbra.pclib.db.migration.compare.ColumnTypeChanged;
import lu.kbra.pclib.db.migration.compare.SchemaChange;
import lu.kbra.pclib.db.transaction.TransactionIsolation;
import lu.kbra.pclib.db.transaction.TransactionOption;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor() {
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, true);
		super.setCapability(DbmsCapability.TABLE_CHARACTER_SET, true);
		super.setCapability(DbmsCapability.TABLE_ENGINE, true);
		super.setCapability(DbmsCapability.COLUMN_ON_UPDATE, true);
		super.setCapability(DbmsCapability.COLUMN_AUTO_INCREMENT, true);
		super.setCapability(DbmsCapability.DATABASE_CHARACTER_SET, true);
		super.setCapability(DbmsCapability.DATABASE_COLLATION, true);
		super.setCapability(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS, true);
		super.setCapability(DbmsCapability.SELECT_FOR_UPDATE_LOCKING, true);
		super.setCapability(DbmsCapability.WHERE_IN_TUPLES, true);
		super.setCapability(DbmsCapability.DEFERRABLE_FOREIGN_KEY, false);
	}

	@Override
	protected List<Pair<MigrationPhase, String[]>> migrateSchemaChange(final SchemaChange change) {
		if (change instanceof ColumnRenamed) {
			final List<Pair<MigrationPhase, String[]>> steps = new ArrayList<>();
			if (!Objects.equals(((ColumnRenamed) change).getOldColumn().getType().getEncodingType().build(),
					((ColumnRenamed) change).getNewColumn().getType().getEncodingType().build())
					|| ((ColumnRenamed) change).getOldColumn().isNullable() != ((ColumnRenamed) change).getNewColumn().isNullable()) {
				steps.add(Tuples.readOnly(MigrationPhase.CHANGE_COLUMNS,
						this.migrateColumnTypeChanged(new ColumnTypeChanged(((ColumnRenamed) change).getTableStructure(),
								((ColumnRenamed) change).getNewColumn(), // new name because CHANGE_COLUMNS is after RENAME_COLUMNS
								((ColumnRenamed) change).getNewColumn()))));
			}
			steps.add(Tuples.readOnly(MigrationPhase.RENAME_COLUMNS, this.migrateColumnRenamed((ColumnRenamed) change)));
			return steps;
		}

		return super.migrateSchemaChange(change);
	}

	@Override
	protected String alterColumnNullable(final TableStructure table, final ColumnData name, final ColumnData newNullable) {
		final String tableName = table.getQualifiedName();
		final String columnName = name.getLocalQualifiedName();

		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + newNullable.getType().getEncodingType().build();
	}

	@Override
	protected String alterColumnType(final TableStructure table, final ColumnData name, final EncodingType<?> newType) {
		final String tableName = table.getQualifiedName();
		final String columnName = name.getLocalQualifiedName();
		System.out.println("[mysql] " + columnName + " -> " + newType.build());

		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + newType.build();
	}

	@Override
	protected String buildDeferrableForeignKey(final DeferMode deferMode) {
		throw new UnsupportedOperationException("MySQL doesn't support DeferMode.");
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
	public String create(final DatabaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasHint(DefaultQueryableHints.CHARACTER_SET)) {
			final String encoding = db.getStringHint(DefaultQueryableHints.CHARACTER_SET);
			sb.append(" CHARACTER SET ").append(this.qualifiedName(encoding));
		}
		if (db.hasHint(DefaultQueryableHints.COLLATION)) {
			final String lcCollate = db.getStringHint(DefaultQueryableHints.COLLATION);
			sb.append(" COLLATE ").append(this.qualifiedName(lcCollate));
		}

		sb.append(";");
		return sb.toString();
	}

	@Override
	public String statementToString(final Statement stmt) {
		if (stmt instanceof ClientPreparedStatement) {
			return ((PreparedQuery) ((ClientPreparedStatement) stmt).getQuery()).asSql();
		}

		return stmt.toString();
	}

	@Override
	protected String escapeEnd() {
		return "`";
	}

	@Override
	protected String escapeStart() {
		return "`";
	}

}
