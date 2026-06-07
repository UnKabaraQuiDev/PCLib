package lu.kbra.pclib.db.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import lu.kbra.pclib.db.exception.DBException;

public abstract class SingleDataBaseConnector extends AbstractDataBaseConnector {

	public final class LockedSingleCachedConnection extends CachedConnection {

		public final class LockedSingleConnectionHolder extends ConnectionHolder {

			private final boolean lockHeld;

			protected LockedSingleConnectionHolder() {
				SingleDataBaseConnector.this.operationLock.lock();
				try {
					LockedSingleCachedConnection.this.incrementUsers();
				} catch (final RuntimeException e) {
					SingleDataBaseConnector.this.operationLock.unlock();
					throw e;
				}
				this.lockHeld = true;
			}

			@Override
			public void close() {
				if (this.holderClosed.compareAndSet(false, true)) {
					try {
						LockedSingleCachedConnection.this.decrementUsers();
					} finally {
						if (this.lockHeld) {
							SingleDataBaseConnector.this.operationLock.unlock();
						}
					}
				}
			}

		}

		public LockedSingleCachedConnection(final Connection connection, final long generation) {
			super(connection, generation);
		}

		@Override
		public ConnectionHolder use() throws DBException {
			if (!this.isUsableFor(SingleDataBaseConnector.this.generation.get())) {
				throw new DBException("Connection is no longer valid for this thread.");
			}
			return new LockedSingleConnectionHolder();
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
				if (SingleDataBaseConnector.this.singleConnection == this) {
					SingleDataBaseConnector.this.singleConnection = null;
				}
			}
		}

		@Override
		void invalidate(final long currentGeneration) throws DBException {
			this.invalidated.set(true);

			if (this.users.get() <= 0) {
				this.forceClose();
			}
		}

	}

	private final ReentrantLock operationLock = new ReentrantLock(true);

	private CachedConnection singleConnection;

	@Override
	public final Connection connect() throws DBException {
		return this.getOrCreateConnection().getConnection();
	}

	@Override
	public boolean keepAlive(final int timeoutSeconds) {
		boolean recreatedAny = false;

		if (this.singleConnection == null) {
			return false;
		}

		try {
			final Connection sqlConnection = this.singleConnection.getConnection();

			if (sqlConnection == null) {
				return false;
			}

			if (!sqlConnection.isValid(timeoutSeconds)) {
				this.invalidateConnection(this.singleConnection);
				recreatedAny = true;
			}
		} catch (final SQLException e) {
			throw new DBException("Exception raised while pinging database.", e);
		}

		return recreatedAny;
	}

	@Override
	public final void reset() throws DBException {
		final long newGeneration = this.generation.incrementAndGet();
		if (this.singleConnection != null && this.singleConnection.getGeneration() < newGeneration) {
			this.singleConnection.close();
			this.singleConnection = null;
		}
	}

	@Override
	public final CachedConnection.ConnectionHolder use() throws DBException {
		return this.getOrCreateConnection().use();
	}

	private synchronized CachedConnection getOrCreateConnection() throws DBException {
		final long currentGeneration = this.generation.get();

		final CachedConnection cached = this.singleConnection;
		if (cached != null && cached.isUsableFor(currentGeneration)) {
			return cached;
		}

		if (cached != null) {
			cached.invalidate(currentGeneration);
			this.singleConnection = null;
		}

		final CachedConnection created = new LockedSingleCachedConnection(this.createConnection(), currentGeneration);
		this.singleConnection = created;
		return created;
	}

	private void invalidateConnection(final CachedConnection cached) {
		cached.invalidate(this.generation.get());

		if (this.singleConnection == cached) {
			this.singleConnection = null;
		}
	}

	@Override
	protected final ConnectionCachingStrategy getConnectionCachingStrategy() {
		return ConnectionCachingStrategy.LOCKED_SINGLE_CONNECTION;
	}

}
