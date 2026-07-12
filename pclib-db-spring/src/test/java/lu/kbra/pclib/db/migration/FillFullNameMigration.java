package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;

@Component
public class FillFullNameMigration implements DataBaseMigration {

	static boolean isMySQL(final DataBaseConnector connector) {
		return "mysql".equalsIgnoreCase(connector.getProtocol());
	}

	static String quote(final DataBaseConnector connector, final String identifier) {
		if (FillFullNameMigration.isMySQL(connector)) {
			return "`" + identifier.replace("`", "``") + "`";
		}
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	static String tableName(final DataBaseConnector connector, final String databaseName, final String tableName) {
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
	public boolean shouldRun(final DataBase dataBase) {
		return dataBase.getDataBaseName().startsWith(MigrationTestConstants.DATABASE_PREFIX);
	}

	@Override
	public void up(final DataBase dataBase, final Connection connection) throws DBException {
		final String firstName = FillFullNameMigration.quote(dataBase.getConnector(), "first_name");
		final String lastName = FillFullNameMigration.quote(dataBase.getConnector(), "last_name");
		final String fullName = FillFullNameMigration.quote(dataBase.getConnector(), "full_name");
		final String value = FillFullNameMigration.isMySQL(dataBase.getConnector()) ? "CONCAT(" + firstName + ", ' ', " + lastName + ")"
				: firstName + " || ' ' || " + lastName;
		final String sql = "UPDATE "
				+ dataBase.getDataBaseEntryUtils().getStructureVisitor().qualifiedName(MigrationTestConstants.TABLE_NAME) + " SET "
				+ dataBase.getDataBaseEntryUtils().getStructureVisitor().qualifiedName(fullName) + " = " + value;

		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Failed to fill full_name column.", e);
		}
	}

}
