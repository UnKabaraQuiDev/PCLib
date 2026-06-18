package lu.kbra.pclib.db.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lu.kbra.pclib.db.exception.DBException;

public abstract class ThreadLocalDataBaseConnector extends AbstractDataBaseConnector {

	public final class PerThreadCachedConnection extends CachedConnection {

		public final class PerThreadConnectionHolder extends ConnectionHolder {

			protected PerThreadConnectionHolder() {
				try {
					PerThreadCachedConnection.this.incrementUsers();
				} catch (final RuntimeException e) {
					throw e;
				}
			}

			@Override
			public void close() {
				if (this.holderClosed.compareAndSet(false, true)) {
					PerThreadCachedConnection.this.decrementUsers();
				}
			}

		}

		public PerThreadCachedConnection(final Connection connection, final long generation) {
			super(connection, generation);
		}

		@Override
		protected void forceClose() {
			if (!this.closed.compareAndSet(false, true)) {
				return;
			}

			try {
				this.connection.close();
			} catch (final SQLException e) {
				throw new DBException("Couldn't close connection (user count=" + this.users.get() + ").", e);
			} finally {
				ThreadLocalDataBaseConnector.this.connections.remove(this);

				final CachedConnection current = ThreadLocalDataBaseConnector.this.threadConnection.get();
				if (current == this) {
					ThreadLocalDataBaseConnector.this.threadConnection.remove();
				}
			}
		}

		@Override
		void invalidate(final long currentGeneration) throws DBException {
			this.invalidated.set(true);

			final CachedConnection current = ThreadLocalDataBaseConnector.this.threadConnection.get();
			if (current == this) {
				ThreadLocalDataBaseConnector.this.threadConnection.remove();
			}

			if (this.users.get() <= 0) {
				this.forceClose();
			}
		}

		@Override
		public ConnectionHolder use() throws DBException {
			if (!this.isUsableFor(ThreadLocalDataBaseConnector.this.generation.get())) {
				throw new DBException("Connection is no longer valid for this thread.");
			}
			return new PerThreadConnectionHolder();
		}

	}

	private final ThreadLocal<CachedConnection> threadConnection = new ThreadLocal<>();
	private final Set<CachedConnection> connections = ConcurrentHashMap.newKeySet();

	private final AtomicLong generation = new AtomicLong(0);

	@Override
	public final Connection connect() throws DBException {
		return this.getOrCreateConnection().getConnection();
	}

	@Override
	protected final ConnectionCachingStrategy getConnectionCachingStrategy() {
		return ConnectionCachingStrategy.PER_THREAD_CACHED;
	}

	private synchronized CachedConnection getOrCreateConnection() throws DBException {
		final long currentGeneration = this.generation.get();

		final CachedConnection cached = this.threadConnection.get();

		if (cached != null && cached.isUsableFor(currentGeneration)) {
			return cached;
		}

		if (cached != null) {
			cached.invalidate(currentGeneration);
			this.threadConnection.remove();
		}

		final CachedConnection created = new PerThreadCachedConnection(this.createConnection(), currentGeneration);
		this.connections.add(created);
		this.threadConnection.set(created);
		return created;
	}

	private void invalidateConnection(final CachedConnection cached) {
		cached.invalidate(this.generation.get());
		this.connections.removeIf(CachedConnection::isFullyClosed);

		final CachedConnection current = this.threadConnection.get();
		if (current == cached) {
			this.threadConnection.remove();
		}
	}

	@Override
	public boolean keepAlive(final int timeoutSeconds) {
		boolean recreatedAny = false;

		for (final CachedConnection cached : this.connections) {
			try {
				final Connection sqlConnection = cached.getConnection();

				if (sqlConnection == null) {
					continue;
				}

				if (!sqlConnection.isValid(timeoutSeconds)) {
					this.invalidateConnection(cached);
					recreatedAny = true;
				}
			} catch (final SQLException e) {
				throw new DBException("Exception raised while pinging database.", e);
			}
		}

		return recreatedAny;
	}

	@Override
	public final void reset() throws DBException {
		final long newGeneration = this.generation.incrementAndGet();

		for (final CachedConnection cached : this.connections) {
			cached.invalidate(newGeneration);
		}

		this.connections.removeIf(CachedConnection::isFullyClosed);

		final CachedConnection current = this.threadConnection.get();
		if (current != null && current.getGeneration() < newGeneration) {
			this.threadConnection.remove();
		}
	}

	@Override
	public final CachedConnection.ConnectionHolder use() throws DBException {
		return this.getOrCreateConnection().use();
	}

}
