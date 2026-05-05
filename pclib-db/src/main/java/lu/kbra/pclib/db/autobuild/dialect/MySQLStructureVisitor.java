package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor(final DataBaseConnector connector) {
		super(connector);
	}

	@Override
	protected String escapeStart() {
		return "`";
	}

	@Override
	protected String escapeEnd() {
		return "`";
	}

	@Override
	protected boolean supportsTableCharacterSet() {
		return true;
	}

	@Override
	protected boolean supportsTableEngine() {
		return true;
	}

	@Override
	protected boolean supportsColumnOnUpdate() {
		return true;
	}

	@Override
	protected boolean supportsColumnAutoIncrement() {
		return true;
	}

	@Override
	protected boolean qualifyCteTablesWithDatabase() {
		return true;
	}

}
