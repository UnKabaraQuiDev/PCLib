package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface SQLQueryVisitor {

	default String qualifiedName(final SQLNamed named) {
		if (named == null) {
			throw new IllegalArgumentException("SQL name cannot be null.");
		}
		return this.qualifiedName(named.getName());
	}

	default String qualifiedName(final String name) {
		return this.quoteIdentifier(name);
	}

	String quoteIdentifier(String identifier);

	default String rawSql(final String sql) {
		return sql;
	}

	default String schemaName(final SQLQueryable<? extends DataBaseEntry> table) {
		return null;
	}

}
