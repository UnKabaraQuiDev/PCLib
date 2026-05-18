package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor(final DataBaseConnector connector) {
		super(connector);
		this.setCapability(DbmsCapability.TABLE_CHARACTER_SET, true);
		this.setCapability(DbmsCapability.TABLE_ENGINE, true);
		this.setCapability(DbmsCapability.COLUMN_ON_UPDATE, true);
		this.setCapability(DbmsCapability.COLUMN_AUTO_INCREMENT, true);
		this.setCapability(DbmsCapability.QUALIFY_CTE_TABLES_WITH_DATABASE, true);
	}

	@Override
	protected String escapeStart() {
		return "`";
	}

	@Override
	protected String escapeEnd() {
		return "`";
	}

}
