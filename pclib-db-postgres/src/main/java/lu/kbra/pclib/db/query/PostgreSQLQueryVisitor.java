package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.autobuild.postgres.PostgreSQLTableHints;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public class PostgreSQLQueryVisitor extends AbstractSQLQueryVisitor {

	public PostgreSQLQueryVisitor() {
		super('"');
	}

	@Override
	public <T extends DataBaseEntry> String schemaName(final SQLQueryable<T> table) {
		return (String) table.getDataBaseEntryUtils()
				.getQueryableHints(table.getTargetClass())
				.getOrDefault(PostgreSQLTableHints.SCHEMA, PostgreSQLDbmsProvider.DEFAULT_SCHEMA);
	}

}
