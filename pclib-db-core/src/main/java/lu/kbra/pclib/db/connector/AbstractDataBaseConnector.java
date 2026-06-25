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
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;

public abstract class AbstractDataBaseConnector implements DataBaseConnector {

	public abstract class CachedConnection implements Closeable {

		public abstract class ConnectionHolder implements AbstractConnection {

			protected final AtomicBoolean holderClosed = new AtomicBoolean(false);

			protected ConnectionHolder() {
			}

			@Override
			public void abort(final Executor executor) throws SQLException {
				CachedConnection.this.connection.abort(executor);
			}

			@Override
			public void clearWarnings() throws SQLException {
				CachedConnection.this.connection.clearWarnings();
			}

			@Override
			public abstract void close();

			@Override
			public void commit() throws SQLException {
				CachedConnection.this.connection.commit();
			}

			@Override
			public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
				return CachedConnection.this.connection.createArrayOf(typeName, elements);
			}

			@Override
			public Blob createBlob() throws SQLException {
				return CachedConnection.this.connection.createBlob();
			}

			@Override
			public Clob createClob() throws SQLException {
				return CachedConnection.this.connection.createClob();
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
			public Statement createStatement() throws SQLException {
				return CachedConnection.this.connection.createStatement();
			}

			@Override
			public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
				return CachedConnection.this.connection.createStatement(resultSetType, resultSetConcurrency);
			}

			@Override
			public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
					throws SQLException {
				return CachedConnection.this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
			}

			@Override
			public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
				return CachedConnection.this.connection.createStruct(typeName, attributes);
			}

			@Override
			public boolean getAutoCommit() throws SQLException {
				return CachedConnection.this.connection.getAutoCommit();
			}

			@Override
			public String getCatalog() throws SQLException {
				return CachedConnection.this.connection.getCatalog();
			}

			@Override
			public Properties getClientInfo() throws SQLException {
				return CachedConnection.this.connection.getClientInfo();
			}

			@Override
			public String getClientInfo(final String name) throws SQLException {
				return CachedConnection.this.connection.getClientInfo(name);
			}

			public Connection getConnection() {
				return CachedConnection.this.connection;
			}

			@Override
			public int getHoldability() throws SQLException {
				return CachedConnection.this.connection.getHoldability();
			}

			@Override
			public DatabaseMetaData getMetaData() throws SQLException {
				return CachedConnection.this.connection.getMetaData();
			}

			@Override
			public int getNetworkTimeout() throws SQLException {
				return CachedConnection.this.connection.getNetworkTimeout();
			}

			@Override
			public String getSchema() throws SQLException {
				return CachedConnection.this.connection.getSchema();
			}

			@Override
			public int getTransactionIsolation() throws SQLException {
				return CachedConnection.this.connection.getTransactionIsolation();
			}

			@Override
			public Map<String, Class<?>> getTypeMap() throws SQLException {
				return CachedConnection.this.connection.getTypeMap();
			}

			@Override
			public SQLWarning getWarnings() throws SQLException {
				return CachedConnection.this.connection.getWarnings();
			}

			@Override
			public boolean isClosed() throws SQLException {
				return CachedConnection.this.connection.isClosed();
			}

			@Override
			public boolean isReadOnly() throws SQLException {
				return CachedConnection.this.connection.isReadOnly();
			}

			@Override
			public boolean isValid(final int timeout) throws SQLException {
				return CachedConnection.this.connection.isValid(timeout);
			}

			@Override
			public boolean isWrapperFor(final Class<?> iface) throws SQLException {
				return CachedConnection.this.connection.isWrapperFor(iface);
			}

			@Override
			public String nativeSQL(final String sql) throws SQLException {
				return CachedConnection.this.connection.nativeSQL(sql);
			}

			@Override
			public CallableStatement prepareCall(final String sql) throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql);
			}

			@Override
			public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
					throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
			}

			@Override
			public CallableStatement
					prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
							throws SQLException {
				return CachedConnection.this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, autoGeneratedKeys);
			}

			@Override
			public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
					throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
			}

			@Override
			public PreparedStatement prepareStatement(
					final String sql,
					final int resultSetType,
					final int resultSetConcurrency,
					final int resultSetHoldability)
					throws SQLException {
				return CachedConnection.this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
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
			public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
				CachedConnection.this.connection.releaseSavepoint(savepoint);
			}

			@Override
			public void rollback() throws SQLException {
				CachedConnection.this.connection.rollback();
			}

			@Override
			public void rollback(final Savepoint savepoint) throws SQLException {
				CachedConnection.this.connection.rollback(savepoint);
			}

			@Override
			public void setAutoCommit(final boolean autoCommit) throws SQLException {
				CachedConnection.this.connection.setAutoCommit(autoCommit);
			}

			@Override
			public void setCatalog(final String catalog) throws SQLException {
				CachedConnection.this.connection.setCatalog(catalog);
			}

			@Override
			public void setClientInfo(final Properties properties) throws SQLClientInfoException {
				CachedConnection.this.connection.setClientInfo(properties);
			}

			@Override
			public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
				CachedConnection.this.connection.setClientInfo(name, value);
			}

			@Override
			public void setHoldability(final int holdability) throws SQLException {
				CachedConnection.this.connection.setHoldability(holdability);
			}

			@Override
			public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
				CachedConnection.this.connection.setNetworkTimeout(executor, milliseconds);
			}

			@Override
			public void setReadOnly(final boolean readOnly) throws SQLException {
				CachedConnection.this.connection.setReadOnly(readOnly);
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
			public void setSchema(final String schema) throws SQLException {
				CachedConnection.this.connection.setSchema(schema);
			}

			@Override
			public void setTransactionIsolation(final int level) throws SQLException {
				CachedConnection.this.connection.setTransactionIsolation(level);
			}

			@Override
			public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
				CachedConnection.this.connection.setTypeMap(map);
			}

			@Override
			public String toString() {
				return "ConnectionHolder@" + System.identityHashCode(this) + " [holderClosed=" + this.holderClosed + "]";
			}

			@Override
			public <T> T unwrap(final Class<T> iface) throws SQLException {
				return CachedConnection.this.connection.unwrap(iface);
			}

		}

		protected transient final AtomicInteger users = new AtomicInteger(0);
		protected transient final Connection connection;
		protected transient final long generation;
		protected transient final AtomicBoolean invalidated = new AtomicBoolean(false);
		protected transient final AtomicBoolean closed = new AtomicBoolean(false);

		protected CachedConnection(final Connection connection, final long generation) {
			this.connection = Objects.requireNonNull(connection, "connection");
			this.generation = generation;
		}

		@Override
		public void close() throws DBException {
			this.invalidate(AbstractDataBaseConnector.this.generation.get());
		}

		protected void decrementUsers() {
			final int remaining = this.users.decrementAndGet();

			if (remaining < 0) {
				this.users.compareAndSet(remaining, 0);
				throw new DBException("ConnectionHolder closed more than once.");
			}

			if (remaining == 0 && this.invalidated.get()) {
				this.forceClose();
			}
		}

		protected abstract void forceClose();

		public Connection getConnection() {
			return this.connection;
		}

		long getGeneration() {
			return this.generation;
		}

		protected void incrementUsers() {
			if (this.closed.get()) {
				throw new DBException("Connection already closed.");
			}
			this.users.incrementAndGet();
		}

		abstract void invalidate(final long currentGeneration) throws DBException;

		boolean isFullyClosed() {
			return this.closed.get();
		}

		public boolean isUsableFor(final long expectedGeneration) throws DBException {
			return this.generation == expectedGeneration && this.isValid();
		}

		public boolean isValid() throws DBException {
			try {
				return !this.closed.get() && !this.invalidated.get() && this.connection != null && !this.connection.isClosed();
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}

		@Override
		public String toString() {
			return "CachedConnection@" + System.identityHashCode(this) + " [users=" + this.users + ", connection=" + this.connection
					+ ", generation=" + this.generation + ", invalidated=" + this.invalidated + ", closed=" + this.closed + "]";
		}

		public abstract CachedConnection.ConnectionHolder use() throws DBException;

	}

	protected enum ConnectionCachingStrategy {
		PER_THREAD_CACHED,
		LOCKED_SINGLE_CONNECTION
	}

	protected transient final AtomicLong generation = new AtomicLong(0);

	@Override
	public abstract AbstractDataBaseConnector clone();

	protected ConnectionCachingStrategy getConnectionCachingStrategy() {
		return ConnectionCachingStrategy.PER_THREAD_CACHED;
	}

}
