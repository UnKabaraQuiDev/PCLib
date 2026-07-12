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

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.DatabaseMigrator;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
public class Database {

	@ToString
	public class AbstractTableTransaction implements DBTransaction {

		protected final ReentrantLock lock = new ReentrantLock(true);

		protected volatile boolean closed = false;
		protected volatile boolean completed = false;

		protected final Connection connection;

		public AbstractTableTransaction() {
			this(Database.this.getConnector().createConnection());
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
		public <X extends DatabaseEntry, V extends DatabaseTable<X>> DatabaseTable<X> use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!Database.this.equals(inst.getDatabase())) {
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

	protected DatabaseConnector connector;
	protected DatabaseEntryUtils databaseEntryUtils;
	protected final String databaseName;
	protected String migrationSchemaName = "pclib_schema_migrations";
	protected DatabaseStructure structure;
	protected final List<AbstractDBTable<? extends DatabaseEntry>> tables = new ArrayList<>();
	protected final List<AbstractDBView<? extends DatabaseEntry>> views = new ArrayList<>();
	@Setter
	protected Map<String, Object> customHints;

	public Database(final DatabaseConnector connector, final String name) {
		this(connector, name, new BaseDatabaseEntryUtils(connector.getProtocol()));
	}

	public Database(final DatabaseConnector connector, final String name, final DatabaseEntryUtils dbEntryUtils) {
		this(connector, name, null, dbEntryUtils);
	}

	public Database(
			final DatabaseConnector connector,
			final String name,
			final Map<String, Object> customHints,
			final DatabaseEntryUtils dbEntryUtils) {
		this.connector = connector;
		this.databaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.databaseEntryUtils = dbEntryUtils;
		this.customHints = customHints == null ? new HashMap<>() : customHints;
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
	}

	public Database clearBeans() {
		this.tables.clear();
		this.views.clear();
		if (this.structure != null) {
			this.structure.getTableStructures().clear();
			this.structure.getViewStructures().clear();
		}
		return this;
	}

	public Database registerTable(final AbstractDBTable<?>... table) {
		Collections.addAll(this.tables, table);
		return this;
	}

	public Database registerView(final AbstractDBView<?>... view) {
		Collections.addAll(this.views, view);
		return this;
	}

	public Database register(final SQLQueryable<?>... instances) {
		for (final SQLQueryable<?> instance : instances) {
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
		final DatabaseScanner scanner = new DatabaseScanner(this.getDatabase(), this.customHints);
		this.tables.forEach(t -> scanner.register(t, t.getCustomHints(), null));
		this.views.forEach(t -> scanner.register(t, t.getCustomHints(), null));
		scanner.doScan();
	}

	public void setDatabaseStructure(final DatabaseStructure databaseStructure) {
		PCUtils.requireNull(this.structure, "DatabaseStructure was already set once.");
		Objects.requireNonNull(databaseStructure, "DatabaseStructure is null.");
		this.structure = databaseStructure;
	}

	public DatabaseStatus create() throws DBException {
		this.validateStructure();

		if (this.connector instanceof ImplicitCreationCapable) {
			final boolean existed = ((ImplicitCreationCapable) this.connector).exists();
			((ImplicitCreationCapable) this.connector).create();
			return new DatabaseStatus(existed, this.getDatabase());
		} else if (this.exists()) {
			this.updateDatabaseConnector();
			return new DatabaseStatus(true, this.getDatabase());
		} else {
			final Connection con = this.connect();

			try (final Statement stmt = con.createStatement()) {
				final String sql = SQLStructureVisitors.forConnector(this.connector).create(this.structure);

				this.requestHook(SQLRequestType.CREATE_DATABASE, sql);

				stmt.executeUpdate(sql);

				this.updateDatabaseConnector();
				return new DatabaseStatus(false, this.getDatabase());
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
	}

	public DBTransaction createTransaction() {
		return new AbstractTableTransaction();
	}

	public Database drop() throws DBException {
		this.validateStructure();

		this.connector.preDelete();
		if (this.connector instanceof ImplicitDeletionCapable) {
			this.connector.reset();
			((ImplicitDeletionCapable) this.connector).delete();
			return this.getDatabase();
		} else {
			final Connection con = this.connect();

			try (final Statement stmt = con.createStatement()) {
				final String sql = SQLStructureVisitors.forConnector(this.connector).drop(this.structure);

				this.requestHook(SQLRequestType.DROP_DATABASE, sql);

				stmt.executeUpdate(sql);

				this.connector.reset();
				this.connector.setDatabase(null);

				return this.getDatabase();
			} catch (final SQLException e) {
				throw new DBException(e);
			}
		}
	}

	public boolean exists() throws DBException {
		this.validateStructure();

		if (this.connector instanceof ImplicitCreationCapable) {
			return ((ImplicitCreationCapable) this.connector).exists();
		} else {
			final Connection con = this.connect();

			try {
				final DatabaseMetaData dbMetaData = con.getMetaData();

				try (final ResultSet rs = dbMetaData.getCatalogs()) {

					while (rs.next()) {
						final String catalogName = rs.getString(1);
						if (catalogName.equals(this.getDatabaseName())) {
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

	@ExperimentalApi
	public int migrate(final Collection<? extends DatabaseMigration> migrations) throws DBException {
		return this.migrate(migrations, this.tables, SchemaMigrationOptions.NONE);
	}

	@ExperimentalApi
	public int migrate(final Collection<? extends DatabaseMigration> migrations, final SchemaMigrationOptions options) throws DBException {
		return this.migrate(migrations, this.tables, options);
	}

	@ExperimentalApi
	public int migrate(
			final Collection<? extends DatabaseMigration> migrations,
			final Collection<? extends AbstractDBTable<?>> tables,
			final SchemaMigrationOptions schemaOptions)
			throws DBException {
		this.updateDatabaseConnector();
		return new DatabaseMigrator(this, migrations, tables, schemaOptions).migrate();
	}

	@ExperimentalApi
	public void migrateSchemas(final Collection<? extends AbstractDBTable<?>> tables, final SchemaMigrationOptions schemaOptions)
			throws DBException {
		this.updateDatabaseConnector();
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

	public void updateDatabaseConnector() throws DBException {
		this.connector.setDatabase(this.databaseName);
		this.connector.reset();
	}

	protected Connection connect() throws DBException {
		return this.connector.connect();
	}

	public Connection createConnection() throws DBException {
		return this.connector.createConnection();
	}

	protected final Database getDatabase() {
		return this;
	}

	protected void validateStructure() {
		if (this.structure == null) {
			throw new DBException(
					"Database hasn't been scanned yet, use Database#register...(...).scanFromBeans() or use an indendent DatabaseScanner.\n"
							+ this.getClass() + " using target "
							+ (this.customHints != null ? this.customHints.getOrDefault(DefaultQueryableHints.TARGET_CLASS, "<unspecified>")
									: "<no custom hints>"),
					null,
					this.structure,
					new IllegalStateException());
		}
	}

	@Override
	public String toString() {
		return "Database [connector=" + this.connector + ", databaseEntryUtils=" + this.databaseEntryUtils + ", databaseName="
				+ this.databaseName + ", migrationSchemaName=" + this.migrationSchemaName + ", structure=" + this.structure + ", tables="
				+ this.tables.size() + ", views=" + this.views.size() + ", customHints=" + this.customHints + "]";
	}

}
