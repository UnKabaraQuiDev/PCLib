package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.autobuild.view.ViewJoinType;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class SQLiteStructureVisitor extends AbstractSQLStructureVisitor {

	public SQLiteStructureVisitor(final DataBaseConnector connector) {
		super(connector);
		this.setCapability(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT, true);
		this.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
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
