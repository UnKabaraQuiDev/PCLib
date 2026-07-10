package lu.kbra.pclib.db.dbms;

import java.sql.Types;
import java.util.Map;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.postgres.PostgreSQLTableHints;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.GeneratedColumnData;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.SQLQueryable;

public class PostgreSQLStructureVisitor extends AbstractSQLStructureVisitor {

	public PostgreSQLStructureVisitor() {
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
	}

	@Override
	public String[] create(final TableStructure table) {
		final String schema = this.schemaName(table);
		final StringBuilder sb = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ").append(this.qualifiedName(schema)).append(";");
		return PCUtils.combineArrays(new String[] { sb.toString() }, super.create(table));
	}

	@Override
	protected String qualifiedStructureName(final TableStructure table) {
		return this.qualifiedName(this.schemaName(table), table.getName());
	}

	@Override
	public String[] create(final ViewStructure view) {
		final String schema = this.schemaName(view);
		final StringBuilder sb = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ").append(this.qualifiedName(schema)).append(";");
		return PCUtils.combineArrays(new String[] { sb.toString() }, super.create(view));
	}

	@Override
	protected String qualifiedStructureName(final ViewStructure view) {
		return this.qualifiedName(this.schemaName(view), view.getName());
	}

	@Override
	public String create(final DataBaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasHint(DefaultTableHints.CHARACTER_SET)) {
			final String encoding = db.<String>getHint(DefaultTableHints.CHARACTER_SET);
			sb.append(" ENCODING ").append(this.qualifiedName(encoding));
		}
		if (db.hasHint(PostgreSQLTableHints.LC_COLLATE)) {
			final String lcCollate = db.<String>getHint(PostgreSQLTableHints.LC_COLLATE);
			sb.append(" LC_COLLATE ").append(this.qualifiedName(lcCollate));
		}
		if (db.hasHint(PostgreSQLTableHints.LC_CTYPE)) {
			final String lcCType = db.<String>getHint(PostgreSQLTableHints.LC_CTYPE);
			sb.append(" LC_CTYPE ").append(this.qualifiedName(lcCType));
		}

		sb.append(';');
		return sb.toString();
	}

	@Override
	public String[] getQueryableNameParts(Class<? extends SQLQueryable<?>> tableClass, Map<String, Object> queryableHints) {
		return new String[] { getSchemaName(tableClass, queryableHints), getQueryableName(tableClass, queryableHints) };
	}

	public String getSchemaName(final Class<? extends SQLQueryable<?>> table, Map<String, Object> hints) {
		return (String) hints.getOrDefault(PostgreSQLTableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

	@Deprecated
	public String schemaName(final TableStructure table) {
		return (String) table.getHints().getOrDefault(PostgreSQLTableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

	@Deprecated
	public String schemaName(final ViewStructure table) {
		return (String) table.getHints().getOrDefault(PostgreSQLTableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
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

}
