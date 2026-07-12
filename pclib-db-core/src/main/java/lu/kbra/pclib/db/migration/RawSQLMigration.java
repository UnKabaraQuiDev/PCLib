package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.DBException;

public class RawSQLMigration implements DatabaseMigration {

	private final int order;
	private final String name;
	private final String upSQL;
	private final String downSQL;

	public RawSQLMigration(final int order, final String name, final String upSQL) {
		this(order, name, upSQL, null);
	}

	public RawSQLMigration(final int order, final String name, final String upSQL, final String downSQL) {
		this.order = order;
		this.name = name;
		this.upSQL = upSQL;
		this.downSQL = downSQL;
	}

	@Override
	public void down(final Database database, final Connection connection) throws DBException {
		if (this.downSQL == null || this.downSQL.trim().isEmpty()) {
			DatabaseMigration.super.down(database, connection);
			return;
		}
		this.execute(connection, this.downSQL, "down");
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public int order() {
		return this.order;
	}

	@Override
	public void up(final Database database, final Connection connection) throws DBException {
		this.execute(connection, this.upSQL, "up");
	}

	private void execute(final Connection connection, final String sql, final String direction) throws DBException {
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Failed to run " + direction + " migration " + this.id() + ".", e);
		}
	}

}
