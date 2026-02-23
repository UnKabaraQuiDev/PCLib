package lu.kbra.pclib.db.connector;

import java.sql.Connection;
import java.sql.SQLException;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.table.DBException;

public abstract class AbstractDataBaseConnector implements DataBaseConnector {

	private Connection connection;

	@Override
	public final Connection connect() throws DBException {
		try {
			return this.connection != null && !this.connection.isClosed() ? this.connection : (this.connection = this.createConnection());
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public final void reset() throws DBException {
		if (this.connection == null) {
			return;
		}
		try {
			this.connection.close();
			this.connection = null;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public abstract AbstractDataBaseConnector clone();

}
