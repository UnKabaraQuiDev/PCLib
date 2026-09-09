package lu.kbra.pclib.db.dbms;

import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.sqlite.jdbc4.JDBC4PreparedStatement;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.domain.dialect.AbstractSQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.transaction.TransactionIsolation;
import lu.kbra.pclib.db.transaction.TransactionOption;

public class SQLiteStructureVisitor extends AbstractSQLStructureVisitor {

	public static final String CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY = SQLiteStructureVisitor.class.getSimpleName()
			+ ".clear_instead_of_truncate";
	public static boolean CLEAR_INSTEAD_OF_TRUNCATE = PCUtils.getBoolean(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY, false);

	public SQLiteStructureVisitor() {
		super.setCapability(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT, true);
		super.setCapability(DbmsCapability.GENERATED_COLUMN_NOT_NULL, false);
		super.setCapability(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS, false);
		super.setCapability(DbmsCapability.SELECT_FOR_UPDATE_LOCKING, false);
		super.setCapability(DbmsCapability.WHERE_IN_TUPLES, false);
		super.setCapability(DbmsCapability.DEFERRABLE_FOREIGN_KEY, true);
	}

	@Override
	protected void buildTransactionOption(final TransactionOption option, final Set<TransactionOption> options2, final List<String> lines) {
		if (option instanceof TransactionIsolation) {
			switch ((TransactionIsolation) option) {
			case READ_UNCOMMITTED:
				lines.add("PRAGMA read_uncommitted = ON;");
				break;

			case READ_COMMITTED:
			case REPEATABLE_READ:
			case SERIALIZABLE:
				lines.add("PRAGMA read_uncommitted = OFF;");
				break;
			}
			return;
		}

		super.buildTransactionOption(option, options2, lines);
	}

	@Override
	public String statementToString(Statement stmt) {
		if (stmt instanceof JDBC4PreparedStatement) {
			return ((JDBC4PreparedStatement) stmt).toString();
		}

		return stmt.toString();
	}

	@Override
	public String create(final DatabaseStructure db) {
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
	protected String joinKeyword(final Table.Type joinType) {
		if (joinType == Table.Type.RIGHT || joinType == Table.Type.FULL) {
			throw new UnsupportedOperationException("SQLite does not support " + joinType.name() + " JOIN.");
		}
		return super.joinKeyword(joinType);
	}

	@Override
	public <T extends DatabaseEntry> String getTruncateSQL(final AbstractDBTable<T> queryable) {
		if (super.getOptionOrDefault(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY,
				SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE)) {
			return "DELETE FROM " + queryable.getQualifiedName() + ";";
		}
		throw new UnsupportedOperationException("SQLite does not support TRUNCATE, use DELETE instead.");
	}

}
