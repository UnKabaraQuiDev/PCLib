package lu.kbra.pclib.db.domain.dialect;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface SQLStructureVisitor {

	String drop(DataBaseStructure dataBaseStructure);

	String drop(TableStructure tableStructure);

	String drop(ViewStructure tableStructure);

	default String fieldToColumnName(final String name) {
		return PCUtils.camelCaseToSnakeCase(name);
	}

	default String qualifiedName(final SQLNamed named) {
		if (named == null) {
			throw new IllegalArgumentException("SQL name cannot be null.");
		}
		if (named instanceof SQLQueryable) {
			return this.qualifiedName((SQLQueryable<?>) named);
		}
		return this.qualifiedName(named.getName());
	}

	default <T extends DataBaseEntry> String qualifiedName(final SQLQueryable<T> table) {
		if (table == null) {
			throw new IllegalArgumentException("SQLQueryable cannot be null.");
		}
		final String schema = this.schemaName(table);
		return schema != null ? this.qualifiedName(schema) + "." + this.qualifiedName(table.getName())
				: this.qualifiedName(table.getName());
	}

	default String qualifiedName(final String name) {
		return this.quoteIdentifier(name);
	}

	String quoteIdentifier(String identifier);

	default String rawSql(final String sql) {
		return sql;
	}

	default <T extends DataBaseEntry> String schemaName(final SQLQueryable<T> table) {
		return null;
	}

	String visit(DataBaseStructure db);

	String visit(TableStructure table);

	String visit(TableStructure table, ColumnData column);

	String visit(ViewStructure view);

}
