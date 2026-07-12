package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.exception.DBException;

@Component
public class FillFullNameMigration implements DatabaseMigration {

	static boolean isMySQL(final DatabaseConnector connector) {
		return "mysql".equalsIgnoreCase(connector.getProtocol());
	}

	static String quote(final DatabaseConnector connector, final String identifier) {
		if (FillFullNameMigration.isMySQL(connector)) {
			return "`" + identifier.replace("`", "``") + "`";
		}
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	static String tableName(final DatabaseConnector connector, final String databaseName, final String tableName) {
		if (FillFullNameMigration.isMySQL(connector)) {
			return FillFullNameMigration.quote(connector, databaseName) + "." + FillFullNameMigration.quote(connector, tableName);
		}
		return FillFullNameMigration.quote(connector, tableName);
	}

	@Override
	public String name() {
		return "fill_full_name";
	}

	@Override
	public int order() {
		return 2;
	}

	@Override
	public boolean shouldRun(final Database database) {
		return database.getDatabaseName().startsWith(MigrationTestConstants.DATABASE_PREFIX);
	}

	@Override
	public void up(final Database database, final Connection connection) throws DBException {
		final String firstName = FillFullNameMigration.quote(database.getConnector(), "first_name");
		final String lastName = FillFullNameMigration.quote(database.getConnector(), "last_name");
		final String fullName = FillFullNameMigration.quote(database.getConnector(), "full_name");
		final String value = FillFullNameMigration.isMySQL(database.getConnector()) ? "CONCAT(" + firstName + ", ' ', " + lastName + ")"
				: firstName + " || ' ' || " + lastName;
		final String sql = "UPDATE "
				+ database.getDatabaseEntryUtils().getStructureVisitor().qualifiedName(MigrationTestConstants.TABLE_NAME) + " SET "
				+ database.getDatabaseEntryUtils().getStructureVisitor().qualifiedName(fullName) + " = " + value;

		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Failed to fill full_name column.", e);
		}
	}

}
