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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;

public abstract class AbstractDataBaseConnector implements DataBaseConnector {

	private final ThreadLocal<CachedConnection> threadConnection = new ThreadLocal<>();
	private final Set<CachedConnection> connections = ConcurrentHashMap.newKeySet();
	private final AtomicLong generation = new AtomicLong(0);

	@Override
	public final Connection connect() throws DBException {
		return this.getOrCreateConnection().getConnection();
	}

	@Override
	public final CachedConnection.ConnectionHolder use() throws DBException {
		return this.getOrCreateConnection().use();
	}

	@Override
	public boolean keepAlive(int timeoutSeconds) {
		boolean recreatedAny = false;

		for (CachedConnection cached : this.connections) {
			try {
				final Connection sqlConnection = cached.getConnection();

				if (sqlConnection == null) {
					continue;
				}

				if (!sqlConnection.isValid(timeoutSeconds)) {
					this.invalidateConnection(cached);
					recreatedAny = true;
				}
			} catch (SQLException e) {
				throw new DBException("Exception raised while pinging database.", e);
			}
		}

		return recreatedAny;
	}

	@Override
	public final void reset() throws DBException {
		final long newGeneration = this.generation.incrementAndGet();

		for (CachedConnection cached : this.connections) {
			cached.invalidate(newGeneration);
		}

		this.connections.removeIf(CachedConnection::isFullyClosed);

		final CachedConnection current = this.threadConnection.get();
		if (current != null && current.getGeneration() < newGeneration) {
			this.threadConnection.remove();
		}
	}

	private CachedConnection getOrCreateConnection() throws DBException {
		final long currentGeneration = this.generation.get();
		CachedConnection cached = this.threadConnection.get();

		if (cached != null && cached.isUsableFor(currentGeneration)) {
			return cached;
		}

		if (cached != null) {
			cached.invalidate(currentGeneration);
			this.threadConnection.remove();
		}

		final CachedConnection created = new CachedConnection(this.createConnection(), currentGeneration);
		this.connections.add(created);
		this.threadConnection.set(created);
		return created;
	}

	private void invalidateConnection(CachedConnection cached) {
		cached.invalidate(this.generation.get());
		this.connections.removeIf(CachedConnection::isFullyClosed);

		final CachedConnection current = this.threadConnection.get();
		if (current == cached) {
			this.threadConnection.remove();
		}
	}

	@Override
	public abstract AbstractDataBaseConnector clone();

	@Override
	public String toString() {
		return "AbstractDataBaseConnector@" + System.identityHashCode(this) + " [threadConnection=" + threadConnection + ", connections="
				+ connections + ", generation=" + generation + "]";
	}

	public final class CachedConnection implements Closeable {

		private final AtomicInteger users = new AtomicInteger(0);
		private final Connection connection;
		private final long generation;
		private final AtomicBoolean invalidated = new AtomicBoolean(false);
		private final AtomicBoolean closed = new AtomicBoolean(false);

		private CachedConnection(final Connection connection, final long generation) {
			this.connection = Objects.requireNonNull(connection, "connection");
			this.generation = generation;
		}

		long getGeneration() {
			return this.generation;
		}

		public Connection getConnection() {
			return this.connection;
		}

		public boolean isValid() throws DBException {
			try {
				return !this.closed.get() && !this.invalidated.get() && this.connection != null && !this.connection.isClosed();
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}

		public boolean isUsableFor(long expectedGeneration) throws DBException {
			return this.generation == expectedGeneration && this.isValid();
		}

		public CachedConnection.ConnectionHolder use() throws DBException {
			if (!this.isUsableFor(AbstractDataBaseConnector.this.generation.get())) {
				throw new DBException("Connection is no longer valid for this thread.");
			}
			return new ConnectionHolder();
		}

		@Override
		public void close() throws DBException {
			this.invalidate(AbstractDataBaseConnector.this.generation.get());
		}

		void invalidate(long currentGeneration) throws DBException {
			this.invalidated.set(true);

			final CachedConnection current = AbstractDataBaseConnector.this.threadConnection.get();
			if (current == this) {
				AbstractDataBaseConnector.this.threadConnection.remove();
			}

			if (this.users.get() <= 0) {
				this.forceClose();
			}
		}

		boolean isFullyClosed() {
			return this.closed.get();
		}

		private void decrementUsers() {
			final int remaining = this.users.decrementAndGet();

			if (remaining < 0) {
				this.users.compareAndSet(remaining, 0);
				throw new DBException("ConnectionHolder closed more than once.");
			}

			if (remaining == 0 && this.invalidated.get()) {
				this.forceClose();
			}
		}

		private void forceClose() {
			if (!this.closed.compareAndSet(false, true)) {
				return;
			}

			try {
				this.connection.close();
			} catch (final SQLException e) {
				throw new DBException("Couldn't close connection (user count=" + this.users.get() + ").", e);
			} finally {
				AbstractDataBaseConnector.this.connections.remove(this);

				final CachedConnection current = AbstractDataBaseConnector.this.threadConnection.get();
				if (current == this) {
					AbstractDataBaseConnector.this.threadConnection.remove();
				}
			}
		}

		private void incrementUsers() {
			if (this.closed.get()) {
				throw new DBException("Connection already closed.");
			}
			this.users.incrementAndGet();
		}

		@Override
		public String toString() {
			return "CachedConnection@" + System.identityHashCode(this) + " [users=" + users + ", connection=" + connection + ", generation="
					+ generation + ", invalidated=" + invalidated + ", closed=" + closed + "]";
		}

		public final class ConnectionHolder implements AutoCloseable, Connection {

			private final AtomicBoolean holderClosed = new AtomicBoolean(false);

			private ConnectionHolder() {
				CachedConnection.this.incrementUsers();
			}

			public Connection getConnection() {
				return CachedConnection.this.connection;
			}

			@Override
			public void close() {
				if (this.holderClosed.compareAndSet(false, true)) {
					CachedConnection.this.decrementUsers();
				}
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

			@Override
			public String toString() {
				return "ConnectionHolder@" + System.identityHashCode(this) + " [holderClosed=" + holderClosed + "]";
			}

		}

	}

}
