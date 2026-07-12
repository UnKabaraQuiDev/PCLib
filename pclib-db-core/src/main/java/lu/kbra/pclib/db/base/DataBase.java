package lu.kbra.pclib.db.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.DataBaseMigrator;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseScanner;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;

@ToString
@Getter
@EqualsAndHashCode
public class DataBase {

	@ToString
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
		public <X extends DataBaseEntry, V extends DataBaseTable<X>> DataBaseTable<X> use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DataBase.this.equals(inst.getDatabase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return inst.createProxy(this.connection);
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

	}

	protected DataBaseConnector connector;
	protected DataBaseEntryUtils dataBaseEntryUtils;
	protected final String dataBaseName;
	protected String migrationSchemaName = "pclib_schema_migrations";
	@Setter
	protected DataBaseStructure dataBaseStructure;
	protected final List<AbstractDBTable<? extends DataBaseEntry>> tables = new ArrayList<>();
	protected final List<AbstractDBView<? extends DataBaseEntry>> views = new ArrayList<>();
	@Setter
	protected Map<String, Object> customHints;

	public DataBase(final DataBaseConnector connector, final String name) {
		this(connector, name, new BaseDataBaseEntryUtils(connector.getProtocol()));
	}

	public DataBase(final DataBaseConnector connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		this(connector, name, null, dbEntryUtils);
	}

	public DataBase(
			final DataBaseConnector connector,
			final String name,
			final Map<String, Object> customHints,
			final DataBaseEntryUtils dbEntryUtils) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = dbEntryUtils;
		this.customHints = customHints == null ? new HashMap<>() : customHints;
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
	}

	public DataBase clearBeans() {
		this.tables.clear();
		this.views.clear();
		if (dataBaseStructure != null) {
			dataBaseStructure.getTableStructures().clear();
			dataBaseStructure.getViewStructures().clear();
		}
		return this;
	}

	public DataBase registerTable(final AbstractDBTable<?>... table) {
		Collections.addAll(this.tables, table);
		return this;
	}

	public DataBase registerView(final AbstractDBView<?>... view) {
		Collections.addAll(this.views, view);
		return this;
	}

	public DataBase register(final SQLQueryable<?>... instances) {
		for (SQLQueryable<?> instance : instances) {
			if (instance instanceof AbstractDBTable<?>) {
				this.registerTable((AbstractDBTable<?>) instance);
			} else if (instance instanceof AbstractDBView<?>) {
				this.registerView((AbstractDBView<?>) instance);
			} else {
				throw new IllegalArgumentException("Unknown SQLQueryable type: " + instance);
			}
		}
		return this;
	}

	public void scanFromBeans() {
		final DataBaseScanner scanner = new DataBaseScanner(this.getDataBase(), this.customHints);
		this.tables.forEach(t -> scanner.register(t));
		this.views.forEach(t -> scanner.register(t));
		scanner.doScan();
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
				final String sql = SQLStructureVisitors.forConnector(this.connector).create(this.dataBaseStructure);

				this.requestHook(SQLRequestType.CREATE_DATABASE, sql);

				stmt.executeUpdate(sql);

				this.updateDataBaseConnector();
				return new DataBaseStatus(false, this.getDataBase());
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
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

	public void migrate(final Collection<? extends DataBaseMigration> migrations) throws DBException {
		new DataBaseMigrator(this, migrations).migrate();
	}

	public void migrate(
			final Collection<? extends DataBaseMigration> migrations,
			final Collection<AbstractDBTable> tables,
			final SchemaMigrationOptions schemaOptions)
			throws DBException {
		this.updateDataBaseConnector();
		new DataBaseMigrator(this, migrations, tables, schemaOptions).migrate();
	}

	public void migrateTables(final Collection<AbstractDBTable> tables, final SchemaMigrationOptions schemaOptions) throws DBException {
		this.updateDataBaseConnector();
		this.migrate(Collections.emptyList(), tables, schemaOptions);
	}

	public void requestHook(final SQLRequestType type, final Object query) {
	}

	public void setMigrationSchemaName(final String migrationSchemaName) {
		if (migrationSchemaName == null || migrationSchemaName.trim().isEmpty()) {
			throw new IllegalArgumentException("Migration schema name cannot be blank.");
		}
		this.migrationSchemaName = migrationSchemaName;
	}

	public void updateDataBaseConnector() throws DBException {
		this.connector.setDatabase(this.dataBaseName);
		this.connector.reset();
	}

	protected Connection connect() throws DBException {
		return this.connector.connect();
	}

	public Connection createConnection() throws DBException {
		return this.connector.createConnection();
	}

	protected final DataBase getDataBase() {
		return this;
	}

}
