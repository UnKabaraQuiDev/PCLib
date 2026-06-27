package lu.kbra.pclib.db.autobuild.sqlite;

import lu.kbra.pclib.db.autobuild.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.DbmsCapability;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.autobuild.view.ViewJoinType;

public class SQLiteStructureVisitor extends AbstractSQLStructureVisitor {

	public SQLiteStructureVisitor() {
		this.setCapability(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT, true);
		this.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
	}

	@Override
	public String visit(final DataBaseStructure db) {
		throw new UnsupportedOperationException("SQLite does not support CREATE DATABASE");
	}

	@Override
	protected String escapeEnd() {
		return "\"";
	}

	@Override
	protected String escapeStart() {
		return "\"";
	}

	@Override
	protected String joinKeyword(final ViewJoinType joinType) {
		if (joinType == ViewJoinType.RIGHT || joinType == ViewJoinType.FULL) {
			throw new UnsupportedOperationException("SQLite does not support " + joinType.name() + " JOIN.");
		}
		return super.joinKeyword(joinType);
	}

}
