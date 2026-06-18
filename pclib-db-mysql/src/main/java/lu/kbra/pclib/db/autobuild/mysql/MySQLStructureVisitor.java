package lu.kbra.pclib.db.autobuild.mysql;

import lu.kbra.pclib.db.autobuild.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.DbmsCapability;
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
	protected String escapeEnd() {
		return "`";
	}

	@Override
	protected String escapeStart() {
		return "`";
	}

}
