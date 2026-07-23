package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

public class MySQLStructureVisitor extends AbstractSQLStructureVisitor {

	public MySQLStructureVisitor() {
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, true);
		super.setCapability(DbmsCapability.TABLE_CHARACTER_SET, true);
		super.setCapability(DbmsCapability.TABLE_ENGINE, true);
		super.setCapability(DbmsCapability.COLUMN_ON_UPDATE, true);
		super.setCapability(DbmsCapability.COLUMN_AUTO_INCREMENT, true);
		super.setCapability(DbmsCapability.QUALIFY_CTE_TABLES_WITH_DATABASE, true);
		super.setCapability(DbmsCapability.DATABASE_CHARACTER_SET, true);
		super.setCapability(DbmsCapability.DATABASE_COLLATION, true);
	}

	@Override
	public String create(final DatabaseStructure db) {
		final StringBuilder sb = new StringBuilder("CREATE DATABASE ");
		sb.append(this.qualifiedName(db.getName()));

		if (db.hasHint(DefaultQueryableHints.CHARACTER_SET)) {
			final String encoding = db.getStringHint(DefaultQueryableHints.CHARACTER_SET);
			sb.append(" CHARACTER SET ").append(this.qualifiedName(encoding));
		}
		if (db.hasHint(DefaultQueryableHints.COLLATION)) {
			final String lcCollate = db.getStringHint(DefaultQueryableHints.COLLATION);
			sb.append(" COLLATE ").append(this.qualifiedName(lcCollate));
		}

		sb.append(";");
		return sb.toString();
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
