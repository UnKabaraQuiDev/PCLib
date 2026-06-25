package lu.kbra.pclib.db.autobuild.mysql;

import lu.kbra.pclib.db.autobuild.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.DbmsCapability;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.autobuild.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.connector.impl.CharacterSetOwner;
import lu.kbra.pclib.db.connector.impl.CollationOwner;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor(final DataBaseConnector connector) {
		super(connector);
		this.setCapability(DbmsCapability.TABLE_CHARACTER_SET, true);
		this.setCapability(DbmsCapability.TABLE_ENGINE, true);
		this.setCapability(DbmsCapability.COLUMN_ON_UPDATE, true);
		this.setCapability(DbmsCapability.COLUMN_AUTO_INCREMENT, true);
		this.setCapability(DbmsCapability.QUALIFY_CTE_TABLES_WITH_DATABASE, true);
		this.setCapability(DbmsCapability.DATABASE_CHARACTER_SET, true);
		this.setCapability(DbmsCapability.DATABASE_COLLATION, true);
	}

	@Override
	protected String escapeEnd() {
		return "`";
	}

	@Override
	protected String escapeStart() {
		return "`";
	}

	@Override
	public String visit(final DataBaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.escape(db.getName()));
		if (this.supports(DbmsCapability.DATABASE_CHARACTER_SET)
				&& (this.connector instanceof CharacterSetOwner || db.hasBaseHint(DefaultTableHints.CHARACTER_SET))) {
			sb.append(" CHARACTER SET ");
			if (this.connector instanceof CharacterSetOwner) {
				sb.append(((CharacterSetOwner) this.connector).getCharacterSet());
			} else {
				sb.append(db.<String>getBaseHint(DefaultTableHints.CHARACTER_SET));
			}
		}
		if (this.supports(DbmsCapability.DATABASE_COLLATION)
				&& (this.connector instanceof CollationOwner || db.hasBaseHint(DefaultTableHints.COLLATION))) {
			sb.append(" COLLATE ");
			if (this.connector instanceof CollationOwner) {
				sb.append(((CollationOwner) this.connector).getCollation());
			} else {
				sb.append(db.<String>getBaseHint(DefaultTableHints.COLLATION));
			}
		}
		sb.append(";");
		return sb.toString();
	}

}
