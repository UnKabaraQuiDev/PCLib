package lu.kbra.pclib.db.autobuild.postgres;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.GeneratedColumnData;
import lu.kbra.pclib.db.autobuild.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.DbmsCapability;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.autobuild.table.meta.DefaultTableHints;

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
		if (column.getType() instanceof IntTypes.BigIntType) {
			return "BIGSERIAL";
		}
		if (column.getType() instanceof IntTypes.SmallIntType) {
			return "SMALLSERIAL";
		}
		return "SERIAL";
	}

	@Override
	public String visit(final DataBaseStructure db) {
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
