package lu.kbra.pclib.db.query;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractSQLQueryVisitor implements SQLQueryVisitor {

	private final char quote;

	protected AbstractSQLQueryVisitor(final char quote) {
		this.quote = quote;
	}

	@Override
	public String quoteIdentifier(final String identifier) {
		if (identifier == null) {
			return null;
		}

		final String trimmed = identifier.trim();
		if (trimmed.isEmpty() || "*".equals(trimmed)) {
			return trimmed;
		}

		if (this.isRawExpression(trimmed)) {
			return this.rawSql(trimmed);
		}

		return Arrays.stream(trimmed.split("\\.")).map(this::quoteIdentifierPart).collect(Collectors.joining("."));
	}

	@Override
	public String rawSql(final String sql) {
		if (sql == null) {
			return null;
		}
		if (this.quote == '`') {
			return sql;
		}
		return sql.replace('`', this.quote);
	}

	protected String quoteIdentifierPart(final String identifier) {
		final String unquoted = this.unquoteIdentifierPart(identifier.trim());
		return String.valueOf(this.quote) + this.escapeIdentifier(unquoted) + this.quote;
	}

	protected String unquoteIdentifierPart(final String identifier) {
		if (identifier.length() >= 2) {
			final char first = identifier.charAt(0);
			final char last = identifier.charAt(identifier.length() - 1);
			if ((first == '`' && last == '`') || (first == '"' && last == '"')) {
				return identifier.substring(1, identifier.length() - 1);
			}
		}
		return identifier;
	}

	protected String escapeIdentifier(final String identifier) {
		return identifier.replace(String.valueOf(this.quote), String.valueOf(this.quote) + this.quote);
	}

	protected boolean isRawExpression(final String identifier) {
		return identifier.indexOf(' ') >= 0 || identifier.indexOf('(') >= 0 || identifier.indexOf(')') >= 0 || identifier.indexOf(',') >= 0
				|| identifier.indexOf('\'') >= 0 || identifier.indexOf(';') >= 0;
	}

}
