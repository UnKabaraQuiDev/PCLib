package lu.kbra.pclib.db.autobuild.postgres;

import java.sql.Types;
import java.util.Objects;

import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.GeneratedColumnData;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public class PostgreSQLStructureVisitor extends AbstractSQLStructureVisitor {

	public PostgreSQLStructureVisitor() {
		this.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
	}

	@Override
	protected String buildColumn(final TableStructure table, final ColumnData column, final boolean inlinePrimaryKey) {
		if (column instanceof GeneratedColumnData) {
			return this.buildGeneratedColumn((GeneratedColumnData) column);
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(this.qualifiedName(column.getName())).append(" ");
		if (column.isAutoIncrement()) {
			sb.append(this.serialType(column));
		} else {
			sb.append(column.getType().build(this));
		}

		if (!column.isNullable()) {
			sb.append(" NOT NULL");
		}

		if (column.getDefaultValue() != null) {
			sb.append(" DEFAULT ").append(column.getDefaultValue());
		}

		return sb.toString();
	}

	@Override
	public <T extends DataBaseEntry> String qualifiedName(final SQLQueryable<T> table) {
		Objects.requireNonNull(table, "SQLQueryable cannot be null.");
		final String schema = schemaName(table);
		return schema != null ? this.qualifiedName(schema, table.getName()) : this.qualifiedName(table.getName());
	}

	public <T extends DataBaseEntry> String schemaName(final SQLQueryable<T> table) {
		return (String) table.getDataBaseEntryUtils()
				.getQueryableHints(table.getTargetClass())
				.getOrDefault(PostgreSQLTableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

	@Override
	protected String buildGeneratedColumn(final GeneratedColumnData column) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.qualifiedName(column.getName())).append(" ").append(column.getType().build(this));
		sb.append(" GENERATED ALWAYS AS (").append(column.getDefaultValue()).append(") STORED");
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
		switch (column.getType().getSQLType()) {
		case Types.BIGINT:
			return "BIGSERIAL";
		case Types.SMALLINT:
			return "SMALLSERIAL";
		default:
			return "SERIAL";
		}
	}

	@Override
	public String create(final DataBaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasBaseHint(DefaultTableHints.CHARACTER_SET)) {
			final String encoding = db.<String>getBaseHint(DefaultTableHints.CHARACTER_SET);
			sb.append(" ENCODING ").append(this.qualifiedName(encoding));
		}
		if (db.hasBaseHint(PostgreSQLTableHints.LC_COLLATE)) {
			final String lcCollate = db.<String>getBaseHint(PostgreSQLTableHints.LC_COLLATE);
			sb.append(" LC_COLLATE ").append(this.qualifiedName(lcCollate));
		}
		if (db.hasBaseHint(PostgreSQLTableHints.LC_CTYPE)) {
			final String lcCType = db.<String>getBaseHint(PostgreSQLTableHints.LC_CTYPE);
			sb.append(" LC_CTYPE ").append(this.qualifiedName(lcCType));
		}

		sb.append(';');
		return sb.toString();
	}

}
