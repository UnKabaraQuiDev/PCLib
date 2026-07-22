package lu.kbra.pclib.db.dbms;

import java.sql.Types;
import java.util.Map;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.postgres.meta.PostgreSQLTableHints;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
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
	public String create(final DatabaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasHint(DefaultQueryableHints.CHARACTER_SET)) {
			final String encoding = db.getStringHint(DefaultQueryableHints.CHARACTER_SET);
			sb.append(" ENCODING ").append(this.qualifiedName(encoding));
		}
		if (db.hasHint(PostgreSQLTableHints.LC_COLLATE)) {
			final String lcCollate = db.getStringHint(PostgreSQLTableHints.LC_COLLATE);
			sb.append(" LC_COLLATE ").append(this.qualifiedName(lcCollate));
		}
		if (db.hasHint(PostgreSQLTableHints.LC_CTYPE)) {
			final String lcCType = db.getStringHint(PostgreSQLTableHints.LC_CTYPE);
			sb.append(" LC_CTYPE ").append(this.qualifiedName(lcCType));
		}

		sb.append(';');
		return sb.toString();
	}

	@Override
	public String[] getQueryableNameParts(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		return new String[] { this.getSchemaName(tableClass, queryableHints), this.getQueryableName(tableClass, queryableHints) };
	}

	public String getSchemaName(final Class<? extends SQLQueryable<?>> table, final Map<String, Object> hints) {
		return (String) hints.getOrDefault(DefaultQueryableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

	public String schemaName(final TableStructure table) {
		return (String) table.getHints().getOrDefault(DefaultQueryableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

	public String schemaName(final ViewStructure table) {
		return (String) table.getHints().getOrDefault(DefaultQueryableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
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
