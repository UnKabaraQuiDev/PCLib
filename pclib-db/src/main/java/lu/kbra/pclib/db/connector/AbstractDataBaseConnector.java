package lu.kbra.pclib.db.connector;

import java.io.Closeable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.table.DBException;

public abstract class AbstractDataBaseConnector implements DataBaseConnector {

	private CachedConnection connection;

	@Override
	public final Connection connect() throws DBException {
		return this.connection != null && this.connection.isValid() ? this.connection.getConnection()
				: (this.connection = new CachedConnection(this.createConnection())).getConnection();
	}

	@Override
	public final ConnectionHolder use() throws DBException {
		if (this.connection == null || !this.connection.isValid()) {
			this.connect();
//			throw new DBException("No active connection, call #connect() first.");
		}

		return this.connection.use();
	}

	@Override
	public final void reset() throws DBException {
		if (this.connection == null) {
			return;
		}
		this.connection.close();
		this.connection = null;
	}

	@Override
	public abstract AbstractDataBaseConnector clone();

	@Override
	public String toString() {
		return "AbstractDataBaseConnector@" + System.identityHashCode(this) + " [connection=" + connection + "]";
	}

	public final class CachedConnection implements Closeable {

		protected final AtomicInteger users = new AtomicInteger(0);
		protected final Connection connection;

		private CachedConnection(final Connection connection) {
			this.connection = connection;
		}

		public synchronized boolean isValid() throws DBException {
			try {
				return this.connection != null && !this.connection.isClosed();
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}

		public Connection getConnection() {
			return this.connection;
		}

		@Override
		public synchronized void close() throws DBException {
			if (this.users.get() <= 0) {
				try {
					this.connection.close();
				} catch (final SQLException e) {
					throw new DBException(e);
				}
			}
		}

		@Override
		public String toString() {
			return "CachedConnection@" + System.identityHashCode(this) + " [users=" + this.users + ", connection=" + this.connection + "]";
		}

		public synchronized ConnectionHolder use() {
			return new ConnectionHolder();
		}

		private void decrementUsers() {
			if (this.users.decrementAndGet() <= 0 && AbstractDataBaseConnector.this.connection != this) {
				this.forceClose();
			}
		}

		private void forceClose() {
			try {
//				connection.rollback();
				this.connection.close();
			} catch (final SQLException e) {
				throw new DBException("Couldn't close connection (user count=" + this.users.get() + ").", e);
			}
		}

		private void incrementUsers() {
			this.users.incrementAndGet();
		}

		public final class ConnectionHolder implements AutoCloseable, Connection {

			private ConnectionHolder() {
				CachedConnection.this.incrementUsers();
			}

			public Connection getConnection() {
				return CachedConnection.this.connection;
			}

			@Override
			public void close() {
				CachedConnection.this.decrementUsers();
			}

			@Override
			public <T> T unwrap(final Class<T> iface) throws SQLException {
				return CachedConnection.this.connection.unwrap(iface);
			}

			@Override
			public boolean isWrapperFor(final Class<?> iface) throws SQLException {
				return CachedConnection.this.connection.isWrapperFor(iface);
			}

			@Override
			public Statement createStatement() throws SQLException {
				return CachedConnection.this.connection.createStatement();
			}

			@Override
			public PreparedStatement prepareStatement(final String sql) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql);
			}

			@Override
			public CallableStatement prepareCall(final String sql) throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql);
			}

			@Override
			public String nativeSQL(final String sql) throws SQLException {
				return CachedConnection.this.connection.nativeSQL(sql);
			}

			@Override
			public void setAutoCommit(final boolean autoCommit) throws SQLException {
				CachedConnection.this.connection.setAutoCommit(autoCommit);
			}

			@Override
			public boolean getAutoCommit() throws SQLException {
				return CachedConnection.this.connection.getAutoCommit();
			}

			@Override
			public void commit() throws SQLException {
				CachedConnection.this.connection.commit();
			}

			@Override
			public void rollback() throws SQLException {
				CachedConnection.this.connection.rollback();
			}

			@Override
			public boolean isClosed() throws SQLException {
				return CachedConnection.this.connection.isClosed();
			}

			@Override
			public DatabaseMetaData getMetaData() throws SQLException {
				return CachedConnection.this.connection.getMetaData();
			}

			@Override
			public void setReadOnly(final boolean readOnly) throws SQLException {
				CachedConnection.this.connection.setReadOnly(readOnly);
			}

			@Override
			public boolean isReadOnly() throws SQLException {
				return CachedConnection.this.connection.isReadOnly();
			}

			@Override
			public void setCatalog(final String catalog) throws SQLException {
				CachedConnection.this.connection.setCatalog(catalog);
			}

			@Override
			public String getCatalog() throws SQLException {
				return CachedConnection.this.connection.getCatalog();
			}

			@Override
			public void setTransactionIsolation(final int level) throws SQLException {
				CachedConnection.this.connection.setTransactionIsolation(level);
			}

			@Override
			public int getTransactionIsolation() throws SQLException {
				return CachedConnection.this.connection.getTransactionIsolation();
			}

			@Override
			public SQLWarning getWarnings() throws SQLException {
				return CachedConnection.this.connection.getWarnings();
			}

			@Override
			public void clearWarnings() throws SQLException {
				CachedConnection.this.connection.clearWarnings();
			}

			@Override
			public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
				return CachedConnection.this.connection.createStatement(resultSetType, resultSetConcurrency);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
					throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
			}

			@Override
			public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
					throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
			}

			@Override
			public Map<String, Class<?>> getTypeMap() throws SQLException {
				return CachedConnection.this.connection.getTypeMap();
			}

			@Override
			public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
				CachedConnection.this.connection.setTypeMap(map);
			}

			@Override
			public void setHoldability(final int holdability) throws SQLException {
				CachedConnection.this.connection.setHoldability(holdability);
			}

			@Override
			public int getHoldability() throws SQLException {
				return CachedConnection.this.connection.getHoldability();
			}

			@Override
			public Savepoint setSavepoint() throws SQLException {
				return CachedConnection.this.connection.setSavepoint();
			}

			@Override
			public Savepoint setSavepoint(final String name) throws SQLException {
				return CachedConnection.this.connection.setSavepoint(name);
			}

			@Override
			public void rollback(final Savepoint savepoint) throws SQLException {
				CachedConnection.this.connection.rollback(savepoint);
			}

			@Override
			public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
				CachedConnection.this.connection.releaseSavepoint(savepoint);
			}

			@Override
			public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
					throws SQLException {
				return CachedConnection.this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
			}

			@Override
			public PreparedStatement prepareStatement(
					final String sql,
					final int resultSetType,
					final int resultSetConcurrency,
					final int resultSetHoldability) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
			}

			@Override
			public CallableStatement prepareCall(
					final String sql,
					final int resultSetType,
					final int resultSetConcurrency,
					final int resultSetHoldability) throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, autoGeneratedKeys);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, columnIndexes);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, columnNames);
			}

			@Override
			public Clob createClob() throws SQLException {
				return CachedConnection.this.connection.createClob();
			}

			@Override
			public Blob createBlob() throws SQLException {
				return CachedConnection.this.connection.createBlob();
			}

			@Override
			public NClob createNClob() throws SQLException {
				return CachedConnection.this.connection.createNClob();
			}

			@Override
			public SQLXML createSQLXML() throws SQLException {
				return CachedConnection.this.connection.createSQLXML();
			}

			@Override
			public boolean isValid(final int timeout) throws SQLException {
				return CachedConnection.this.connection.isValid(timeout);
			}

			@Override
			public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
				CachedConnection.this.connection.setClientInfo(name, value);
			}

			@Override
			public void setClientInfo(final Properties properties) throws SQLClientInfoException {
				CachedConnection.this.connection.setClientInfo(properties);
			}

			@Override
			public String getClientInfo(final String name) throws SQLException {
				return CachedConnection.this.connection.getClientInfo(name);
			}

			@Override
			public Properties getClientInfo() throws SQLException {
				return CachedConnection.this.connection.getClientInfo();
			}

			@Override
			public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
				return CachedConnection.this.connection.createArrayOf(typeName, elements);
			}

			@Override
			public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
				return CachedConnection.this.connection.createStruct(typeName, attributes);
			}

			@Override
			public void setSchema(final String schema) throws SQLException {
				CachedConnection.this.connection.setSchema(schema);
			}

			@Override
			public String getSchema() throws SQLException {
				return CachedConnection.this.connection.getSchema();
			}

			@Override
			public void abort(final Executor executor) throws SQLException {
				CachedConnection.this.connection.abort(executor);
			}

			@Override
			public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
				CachedConnection.this.connection.setNetworkTimeout(executor, milliseconds);
			}

			@Override
			public int getNetworkTimeout() throws SQLException {
				return CachedConnection.this.connection.getNetworkTimeout();
			}

		}

	}

}
