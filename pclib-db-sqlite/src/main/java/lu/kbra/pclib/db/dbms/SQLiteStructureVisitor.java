package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.view.ViewTable.Type;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class SQLiteStructureVisitor extends AbstractSQLStructureVisitor {

	public static final String CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY = SQLiteStructureVisitor.class.getSimpleName()
			+ ".clear_instead_of_truncate";
	public static final boolean CLEAR_INSTEAD_OF_TRUNCATE = PCUtils.getBoolean(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY,
			false);

	public SQLiteStructureVisitor() {
		super.setCapability(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT, true);
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
	}

	@Override
	public String create(final DataBaseStructure db) {
		throw new UnsupportedOperationException("SQLite does not support CREATE DATABASE.");
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
	protected String joinKeyword(final ViewTable.Type joinType) {
		if (joinType == ViewTable.Type.RIGHT || joinType == ViewTable.Type.FULL) {
			throw new UnsupportedOperationException("SQLite does not support " + joinType.name() + " JOIN.");
		}
		return super.joinKeyword(joinType);
	}

	@Override
	public <T extends DataBaseEntry> String getTruncateSQL(final AbstractDBTable<T> queryable) {
		if (super.getOptionOrDefault(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY,
				SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE)) {
			return "DELETE FROM " + queryable.getQualifiedName() + ";";
		}
		throw new UnsupportedOperationException("SQLite does not support TRUNCATE, use DELETE instead.");
	}

}
