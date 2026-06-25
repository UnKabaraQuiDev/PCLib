package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public class PostgreSQLQueryVisitor extends AbstractSQLQueryVisitor {

	public PostgreSQLQueryVisitor() {
		super('"');
	}

	@Override
	public String schemaName(final SQLQueryable<? extends DataBaseEntry> table) {
		// TODO: this should come from the meta-annotations
		return "public";
	}

}
