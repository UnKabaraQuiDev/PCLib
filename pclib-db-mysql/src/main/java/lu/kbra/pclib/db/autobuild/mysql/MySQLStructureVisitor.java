package lu.kbra.pclib.db.autobuild.mysql;

import lu.kbra.pclib.db.autobuild.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.DbmsCapability;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.autobuild.table.meta.DefaultTableHints;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor() {
		this.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, Boolean.TRUE);
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
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasBaseHint(DefaultTableHints.CHARACTER_SET)) {
			final String encoding = db.<String>getBaseHint(DefaultTableHints.CHARACTER_SET);
			sb.append(" CHARACTER SET ").append(this.qualifiedName(encoding));
		}
		if (db.hasBaseHint(DefaultTableHints.COLLATION)) {
			final String lcCollate = db.<String>getBaseHint(DefaultTableHints.COLLATION);
			sb.append(" COLLATE ").append(this.qualifiedName(lcCollate));
		}

		sb.append(";");
		return sb.toString();
	}

}
