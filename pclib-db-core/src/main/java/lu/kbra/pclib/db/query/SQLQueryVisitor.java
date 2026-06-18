package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.impl.SQLNamed;

public interface SQLQueryVisitor {

	default String qualifiedName(final SQLNamed named) {
		if (named == null) {
			throw new IllegalArgumentException("SQL name cannot be null.");
		}
		return this.rawSql(named.getQualifiedName());
	}

	default String qualifiedName(final String name) {
		return this.quoteIdentifier(name);
	}

	String quoteIdentifier(String identifier);

	default String rawSql(final String sql) {
		return sql;
	}

}
