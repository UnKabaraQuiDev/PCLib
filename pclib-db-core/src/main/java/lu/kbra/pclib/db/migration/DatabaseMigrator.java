package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.exception.RollbackFailedException;
import lu.kbra.pclib.db.table.AbstractDBTable;

@ExperimentalApi
public class DatabaseMigrator {

	private final Database database;
	private final List<DatabaseMigration> migrations;
	private final Collection<? extends AbstractDBTable<?>> tables;
	private final SchemaMigrationOptions schemaOptions;

	public DatabaseMigrator(final Database database, final Collection<? extends DatabaseMigration> migrations) {
		this(database, migrations, database.getTables(), SchemaMigrationOptions.NONE);
	}

	public DatabaseMigrator(
			final Database database,
			final Collection<? extends DatabaseMigration> migrations,
			final Collection<? extends AbstractDBTable<?>> tables,
			final SchemaMigrationOptions schemaOptions) {
		this.database = database;
		this.migrations = migrations == null ? Collections.emptyList()
				: migrations.stream()
						.sorted(Comparator.comparingInt(DatabaseMigration::order).thenComparing(DatabaseMigration::name))
						.collect(Collectors.toList());
		this.tables = tables == null ? Collections.emptyList() : tables;
		this.schemaOptions = schemaOptions == null ? SchemaMigrationOptions.NONE : schemaOptions;
	}

	public int migrate() throws DBException {
		int count = 0;
		try (AbstractConnection connection = this.database.use()) {
			final boolean previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try {
				this.ensureMigrationTable(connection);

				new DatabaseSchemaMigrator().migrate(connection, this.tables, this.schemaOptions);

				final Set<String> applied = this.loadAppliedMigrationIds(connection);
				for (final DatabaseMigration migration : this.migrations) {
					if (applied.contains(migration.id()) || !migration.shouldRun(this.database)) {
						continue;
					}
					migration.up(this.database, connection);
					count++;
					this.insertAppliedMigration(connection, migration);
				}

				connection.commit();
			} catch (final Exception e) {
				connection.rollback();
				if (e instanceof DBException) {
					throw (DBException) e;
				}
				throw new RollbackFailedException("Database migration failed.", null, database.getStructure(), e);
			} finally {
				connection.setAutoCommit(previousAutoCommit);
			}
		} catch (final SQLException e) {
			throw new InternalDBException("Database migration failed.", null, database.getStructure(), e);
		}

		return count;
	}

	private void ensureMigrationTable(final Connection connection) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			// TODO: make this not dbms specific
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.migrationTableName()
					+ " (id VARCHAR(255) PRIMARY KEY, migration_order INTEGER NOT NULL, name VARCHAR(255) NOT NULL, applied_at VARCHAR(64) NOT NULL);");
		}
	}

	private void insertAppliedMigration(final Connection connection, final DatabaseMigration migration) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(
				"INSERT INTO " + this.migrationTableName() + " (id, migration_order, name, applied_at) VALUES (?, ?, ?, ?)")) {
			stmt.setString(1, migration.id());
			stmt.setInt(2, migration.order());
			stmt.setString(3, migration.name());
			stmt.setString(4, Instant.now().toString());
			stmt.executeUpdate();
		}
	}

	private Set<String> loadAppliedMigrationIds(final Connection connection) throws SQLException {
		final Set<String> ids = new LinkedHashSet<>();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT id FROM " + this.migrationTableName() + " ORDER BY migration_order ASC")) {
			while (rs.next()) {
				ids.add(rs.getString("id"));
			}
		}
		return ids;
	}

	private String migrationTableName() {
		return this.database.getDatabaseEntryUtils().getStructureVisitor().qualifiedName(this.database.getMigrationSchemaName());
	}

	@Override
	public String toString() {
		return "DatabaseMigrator@" + System.identityHashCode(this) + " [database=" + this.database + ", migrations="
				+ this.migrations.stream().map(DatabaseMigration::id).collect(Collectors.toList()) + "]";
	}

}
