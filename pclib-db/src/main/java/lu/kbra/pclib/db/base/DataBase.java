package lu.kbra.pclib.db.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.connector.AbstractConnection;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.table.transaction.DBTransaction;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBase {

	protected DataBaseConnector connector;
	protected DataBaseEntryUtils dataBaseEntryUtils;

	protected final String dataBaseName;

	protected final Map<String, AbstractDBTable<?>> tableBeans = new HashMap<>();

	public DataBase(final DataBaseConnectorFactory connector, final String name) {
		this(connector.get(), name);
	}

	public DataBase(final DataBaseConnector connector, final String name) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = new BaseDataBaseEntryUtils();
	}

	public DataBase(final DataBaseConnectorFactory connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		this(connector.get(), name, dbEntryUtils);
	}

	public DataBase(final DataBaseConnector connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = dbEntryUtils;
	}

	public DataBase(final DataBaseConnectorFactory connector, final String name, final String charSet, final String collation) {
		this(connector.get(), name, charSet, collation);
	}

	public DataBase(final DataBaseConnector connector, final String name, final String charSet, final String collation) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof CharacterSetCapable) {
			((CharacterSetCapable) this.connector).setCharacterSet(charSet);
		}
		if (connector instanceof CollationCapable) {
			((CollationCapable) this.connector).setCollation(collation);
		}
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = new BaseDataBaseEntryUtils();
	}

	public DataBase(final DataBaseConnectorFactory connector, final String name, final String charSet, final String collation,
			final DataBaseEntryUtils dbEntryUtils) {
		this(connector.get(), name, charSet, collation, dbEntryUtils);
	}

	public DataBase(final DataBaseConnector connector, final String name, final String charSet, final String collation,
			final DataBaseEntryUtils dbEntryUtils) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof CharacterSetCapable) {
			((CharacterSetCapable) this.connector).setCharacterSet(charSet);
		}
		if (connector instanceof CollationCapable) {
			((CollationCapable) this.connector).setCollation(collation);
		}
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}

		this.dataBaseEntryUtils = dbEntryUtils;
	}

	public void requestHook(final SQLRequestType type, final Object query) {
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

	public DataBaseStatus create() throws DBException {
		if (this.connector instanceof ImplicitCreationCapable) {
			final boolean existed = ((ImplicitCreationCapable) this.connector).exists();
			((ImplicitCreationCapable) this.connector).create();
			return new DataBaseStatus(existed, this.getDataBase());
		} else {
			if (this.exists()) {
				this.updateDataBaseConnector();
				return new DataBaseStatus(true, this.getDataBase());
			} else {
				final Connection con = this.connect();

				try (final Statement stmt = con.createStatement()) {

					final String sql = this.getCreateSQL();

					this.requestHook(SQLRequestType.CREATE_DATABASE, sql);

					stmt.executeUpdate(sql);

					this.updateDataBaseConnector();
					return new DataBaseStatus(false, this.getDataBase());
				} catch (final SQLException e) {
					throw new DBException(e);
				}
			}
		}
	}

	public DataBase drop() throws DBException {
		if (this.connector instanceof ImplicitDeletionCapable) {
			this.connector.reset();
			((ImplicitDeletionCapable) this.connector).delete();
			return this.getDataBase();
		} else {
			final Connection con = this.connect();

			try (final Statement stmt = con.createStatement()) {

				final String sql = "DROP DATABASE IF EXISTS `" + this.getDataBaseName() + "`;";

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

	public void updateDataBaseConnector() throws DBException {
		this.connector.setDatabase(this.dataBaseName);
		this.connector.reset();
	}

	private DataBase getDataBase() {
		return this;
	}

	public String getCreateSQL() {
		return "CREATE DATABASE `" + this.getDataBaseName() + "`"
				+ (this.connector instanceof CharacterSetCapable
						? " CHARACTER SET " + ((CharacterSetCapable) this.connector).getCharacterSet()
						: "")
				+ (this.connector instanceof CollationCapable ? " COLLATE " + ((CollationCapable) this.connector).getCollation() : "")
				+ ";";
	}

	protected Connection connect() throws DBException {
		return this.connector.connect();
	}

	protected Connection createConnection() throws DBException {
		return this.connector.createConnection();
	}

	public <T extends AbstractDBTable<?>> T getTableBean(final Class<T> t) {
		return (T) this.tableBeans.get(TableStructure.classToTableName(this.dataBaseEntryUtils.getEntryType(t)));
	}

	public <T extends AbstractDBTable<?>> T getTableBean(final String tableName) {
		return (T) this.tableBeans.get(tableName);
	}

	public <T extends AbstractDBTable<?>> void registerTableBean(final T t) {
		this.tableBeans.put(t.getName(), t);
	}

	public Map<String, AbstractDBTable<?>> getTableBeans() {
		return this.tableBeans;
	}

	public String getDataBaseName() {
		return this.dataBaseName;
	}

	public Supplier<AbstractConnection> getConnectionSupplier() {
		return () -> this.getConnector().use();
	}

	public DataBaseConnector getConnector() {
		return this.connector;
	}

	public DataBaseEntryUtils getDataBaseEntryUtils() {
		return this.dataBaseEntryUtils;
	}

	public DBTransaction createTransaction() {
		return new AbstractTableTransaction();
	}

	public class AbstractTableTransaction implements DBTransaction {

		protected final ReentrantLock lock = new ReentrantLock(true);

		protected volatile boolean closed = false;
		protected volatile boolean completed = false;

		protected final Connection connection;

		public AbstractTableTransaction(final Connection connection) {
			this.connection = connection;

			try {
				connection.setAutoCommit(false);
			} catch (final SQLException e) {
				throw new DBException("Couldn't configure connection for transaction.", e);
			}
		}

		public AbstractTableTransaction() {
			this(DataBase.this.getConnector().createConnection());
		}

		@Override
		public <X extends DataBaseEntry, V extends DataBaseTable<X>> DataBaseTable<X> use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!inst.getDataBase().equals(DataBase.this)) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return inst.createProxy(connection);
		}

		@Override
		public <X extends DataBaseEntry, V extends NTDataBaseTable<X>> NTDataBaseTable<X> use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!inst.getDataBase().equals(DataBase.this)) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return inst.createProxy(connection);
		}

		@Override
		public Connection getConnection() {
			return this.connection;
		}

		protected void ensureOpen() {
			if (this.closed) {
				throw new IllegalStateException("Transaction already closed.");
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

		protected void executeLocked(final Runnable action) throws DBException {
			this.lock.lock();
			try {
				this.ensureOpen();
				action.run();
			} finally {
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
		public boolean isClosed() {
			return this.closed;
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
		public String toString() {
			return "AbstractTableTransaction@" + System.identityHashCode(this) + " [lock=" + this.lock + ", closed=" + this.closed
					+ ", completed=" + this.completed + ", connection=" + this.connection + "]";
		}

	}

	@Override
	public String toString() {
		return "DataBase@" + System.identityHashCode(this) + " [connector=" + this.connector + ", dataBaseName=" + this.dataBaseName + "]";
	}

}
