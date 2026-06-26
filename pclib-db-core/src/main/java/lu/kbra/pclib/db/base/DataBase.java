package lu.kbra.pclib.db.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.connector.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.DataBaseMigrator;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.transaction.DBTransaction;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBase {

	public class AbstractTableTransaction implements DBTransaction {

		protected final ReentrantLock lock = new ReentrantLock(true);

		protected volatile boolean closed = false;
		protected volatile boolean completed = false;

		protected final Connection connection;

		public AbstractTableTransaction() {
			this(DataBase.this.getConnector().createConnection());
		}

		public AbstractTableTransaction(final Connection connection) {
			this.connection = connection;

			try {
				connection.setAutoCommit(false);
			} catch (final SQLException e) {
				throw new DBException("Couldn't configure connection for transaction.", e);
			}
		}

		@Override
		public void close() throws DBException {
			this.lock.lock();
			try {
				if (this.closed) {
					return;
				}

				try {
					if (!this.completed) {
						this.connection.rollback();
						this.completed = true;
					}
				} catch (final SQLException e) {
					throw new DBException("Couldn't rollback transaction during close.", e);
				}
			} finally {
				try {
					this.connection.close();
				} catch (final SQLException e) {
					throw new DBException("Couldn't close transaction connection.", e);
				} finally {
					this.closed = true;
				}
				this.lock.unlock();
			}
		}

		@Override
		public void commit() throws DBException {
			this.executeLocked(() -> {
				try {
					this.connection.commit();
					this.completed = true;
				} catch (final SQLException e) {
					throw new DBException("Couldn't commit transaction.", e);
				}
			});
		}

		protected void ensureOpen() {
			if (this.closed) {
				throw new IllegalStateException("Transaction already closed.");
			}
		}

		protected void executeLocked(final Runnable action) throws DBException {
			this.lock.lock();
			try {
				this.ensureOpen();
				action.run();
			} finally {
				this.lock.unlock();
			}
		}

		protected <B> B executeLocked(final Supplier<B> action) throws DBException {
			this.lock.lock();
			try {
				this.ensureOpen();
				return action.get();
			} finally {
				this.lock.unlock();
			}
		}

		@Override
		public Connection getConnection() {
			return this.connection;
		}

		@Override
		public boolean isClosed() {
			return this.closed;
		}

		@Override
		public void rollback() throws DBException {
			this.executeLocked(() -> {
				try {
					this.connection.rollback();
					this.completed = true;
				} catch (final SQLException e) {
					throw new DBException("Couldn't rollback transaction.", e);
				}
			});
		}

		@Override
		public String toString() {
			return "AbstractTableTransaction@" + System.identityHashCode(this) + " [lock=" + this.lock + ", closed=" + this.closed
					+ ", completed=" + this.completed + ", connection=" + this.connection + "]";
		}

		@Override
		public <X extends DataBaseEntry, V extends DataBaseTable<X>> DataBaseTable<X> use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DataBase.this.equals(inst.getDatabase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return inst.createProxy(this.connection);
		}

	}

	protected DataBaseConnector connector;
	protected DataBaseEntryUtils dataBaseEntryUtils;
	protected final String dataBaseName;
	protected String migrationSchemaName = "pclib_schema_migrations";
	protected DataBaseStructure dataBaseStructure;

	public DataBase(final DataBaseConnector connector, final String name) {
		this(connector, name, new BaseDataBaseEntryUtils(connector.getProtocol()));
	}

	public DataBase(final DataBaseConnector connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		this(connector, name, null, dbEntryUtils);
	}

	public DataBase(
			final DataBaseConnector connector,
			final String name,
			final Map<String, Object> baseHints,
			final DataBaseEntryUtils dbEntryUtils) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = dbEntryUtils;
		this.dataBaseStructure = this.dataBaseEntryUtils.scanDataBase(this, baseHints == null ? new HashMap<>(0) : baseHints);
	}

	protected Connection connect() throws DBException {
		return this.connector.connect();
	}

	public DataBaseStatus create() throws DBException {
		if (this.connector instanceof ImplicitCreationCapable) {
			final boolean existed = ((ImplicitCreationCapable) this.connector).exists();
			((ImplicitCreationCapable) this.connector).create();
			return new DataBaseStatus(existed, this.getDataBase());
		} else if (this.exists()) {
			this.updateDataBaseConnector();
			return new DataBaseStatus(true, this.getDataBase());
		} else {
			final Connection con = this.connect();

			try (final Statement stmt = con.createStatement()) {
				final String sql = SQLStructureVisitors.forConnector(this.connector).visit(this.dataBaseStructure);

				this.requestHook(SQLRequestType.CREATE_DATABASE, sql);

				stmt.executeUpdate(sql);

				this.updateDataBaseConnector();
				return new DataBaseStatus(false, this.getDataBase());
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
	}

	protected Connection createConnection() throws DBException {
		return this.connector.createConnection();
	}

	public DBTransaction createTransaction() {
		return new AbstractTableTransaction();
	}

	public DataBase drop() throws DBException {
		this.connector.preDelete();
		if (this.connector instanceof ImplicitDeletionCapable) {
			this.connector.reset();
			((ImplicitDeletionCapable) this.connector).delete();
			return this.getDataBase();
		} else {
			final Connection con = this.connect();

			try (final Statement stmt = con.createStatement()) {
				final String sql = SQLStructureVisitors.forConnector(this.connector).drop(this.dataBaseStructure);

				this.requestHook(SQLRequestType.DROP_DATABASE, sql);

				stmt.executeUpdate(sql);

				this.connector.reset();
				this.connector.setDatabase(null);

				return this.getDataBase();
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
	}

	public boolean exists() throws DBException {
		if (this.connector instanceof ImplicitCreationCapable) {
			return ((ImplicitCreationCapable) this.connector).exists();
		} else {
			final Connection con = this.connect();

			try {
				final DatabaseMetaData dbMetaData = con.getMetaData();

				try (final ResultSet rs = dbMetaData.getCatalogs()) {

					while (rs.next()) {
						final String catalogName = rs.getString(1);
						if (catalogName.equals(this.getDataBaseName())) {
							rs.close();

							return true;
						}
					}

					rs.close();

					return false;
				}
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
	}

	@Deprecated
	public Supplier<AbstractConnection> getConnectionSupplier() {
		return () -> this.getConnector().use();
	}

	public DataBaseConnector getConnector() {
		return this.connector;
	}

	private DataBase getDataBase() {
		return this;
	}

	public DataBaseEntryUtils getDataBaseEntryUtils() {
		return this.dataBaseEntryUtils;
	}

	public String getDataBaseName() {
		return this.dataBaseName;
	}

	public String getMigrationSchemaName() {
		return this.migrationSchemaName;
	}

	public void migrate(final Collection<? extends DataBaseMigration> migrations) throws DBException {
		new DataBaseMigrator(this, migrations).migrate();
	}

	public void migrate(
			final Collection<? extends DataBaseMigration> migrations,
			final Collection<? extends DataBaseTable<? extends DataBaseEntry>> tables,
			final SchemaMigrationOptions schemaOptions)
			throws DBException {
		new DataBaseMigrator(this, migrations, tables, schemaOptions).migrate();
	}

	public void migrateTables(
			final Collection<? extends DataBaseTable<? extends DataBaseEntry>> tables,
			final SchemaMigrationOptions schemaOptions)
			throws DBException {
		this.migrate(List.of(), tables, schemaOptions);
	}

	public Connection openConnection() throws DBException {
		return this.createConnection();
	}

	public void requestHook(final SQLRequestType type, final Object query) {
	}

	public void setMigrationSchemaName(final String migrationSchemaName) {
		if (migrationSchemaName == null || migrationSchemaName.isBlank()) {
			throw new IllegalArgumentException("Migration schema name cannot be blank.");
		}
		this.migrationSchemaName = migrationSchemaName;
	}

	@Override
	public String toString() {
		return "DataBase@" + System.identityHashCode(this) + " [connector=" + this.connector + ", dataBaseName=" + this.dataBaseName + "]";
	}

	public void updateDataBaseConnector() throws DBException {
		this.connector.setDatabase(this.dataBaseName);
		this.connector.reset();
	}

}
