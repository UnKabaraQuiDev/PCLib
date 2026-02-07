package lu.kbra.pclib.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDataBaseConnector implements DataBaseConnector {

	private Connection connection;

	@Override
	public final Connection connect() throws SQLException {
		return this.connection != null && !this.connection.isClosed() ? this.connection : (this.connection = this.createConnection());
	}

	@Override
	public final void reset() throws SQLException {
		if (this.connection == null) {
			return;
		}
		this.connection.close();
		this.connection = null;
	}

}
