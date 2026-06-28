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

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.DataBaseTable;

public class DataBaseMigrator {

	private final DataBase dataBase;
	private final List<DataBaseMigration> migrations;
	private final Collection<? extends DataBaseTable<? extends DataBaseEntry>> tables;
	private final SchemaMigrationOptions schemaOptions;

	public DataBaseMigrator(final DataBase dataBase, final Collection<? extends DataBaseMigration> migrations) {
		this(dataBase, migrations, Collections.emptyList(), SchemaMigrationOptions.NONE);
	}

	public DataBaseMigrator(
			final DataBase dataBase,
			final Collection<? extends DataBaseMigration> migrations,
			final Collection<? extends DataBaseTable<? extends DataBaseEntry>> tables,
			final SchemaMigrationOptions schemaOptions) {
		this.dataBase = dataBase;
		this.migrations = migrations == null ? Collections.emptyList()
				: migrations.stream()
						.sorted(Comparator.comparingInt(DataBaseMigration::order).thenComparing(DataBaseMigration::name))
						.collect(Collectors.toList());
		this.tables = tables == null ? Collections.emptyList() : tables;
		this.schemaOptions = schemaOptions == null ? SchemaMigrationOptions.NONE : schemaOptions;
	}

	public void migrate() throws DBException {
		try (Connection connection = this.dataBase.openConnection()) {
			final boolean previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try {
				this.ensureMigrationTable(connection);

				final Set<String> applied = this.loadAppliedMigrationIds(connection);
				for (final DataBaseMigration migration : this.migrations) {
					if (applied.contains(migration.id()) || !migration.shouldRun(this.dataBase)) {
						continue;
					}
					migration.up(this.dataBase, connection);
					this.insertAppliedMigration(connection, migration);
				}

				new DataBaseSchemaMigrator(this.dataBase.getConnector()).migrate(connection, this.tables, this.schemaOptions);

				connection.commit();
			} catch (final Exception e) {
				connection.rollback();
				if (e instanceof DBException) {
					throw (DBException) e;
				}
				throw new DBException("Database migration failed.", e);
			} finally {
				connection.setAutoCommit(previousAutoCommit);
			}
		} catch (final SQLException e) {
			throw new DBException("Database migration failed.", e);
		}
	}

	private String migrationTableName() {
		return dataBase.getDataBaseEntryUtils().getStructureVisitor().qualifiedName(this.dataBase.getMigrationSchemaName());
	}

	@Override
	public String toString() {
		return "DataBaseMigrator@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", migrations="
				+ this.migrations.stream().map(DataBaseMigration::id).collect(Collectors.toList()) + "]";
	}

	private void ensureMigrationTable(final Connection connection) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.migrationTableName() + " (" + "id VARCHAR(255) PRIMARY KEY, "
					+ "migration_order INTEGER NOT NULL, " + "name VARCHAR(255) NOT NULL, " + "applied_at VARCHAR(64) NOT NULL" + ");");
		}
	}

	private void insertAppliedMigration(final Connection connection, final DataBaseMigration migration) throws SQLException {
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

}
