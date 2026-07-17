package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class DatabaseSchemaMigrator {

	public void
			migrate(final Connection connection, final Iterable<? extends AbstractDBTable<?>> tables, final SchemaMigrationOptions options)
					throws DBException {
		if (options == null || !options.isAutoAddColumns() && !options.isAutoRemoveColumns()) {
			return;
		}

		for (final AbstractDBTable<?> table : tables) {

			final Set<String> current = this.currentColumns(connection, table);
			final Set<String> expected = Arrays.stream(table.getStructure().getColumns())
					.map(ColumnData::getLocalName)
					.map(this::normalize)
					.collect(Collectors.toCollection(LinkedHashSet::new));

			if (options.isAutoAddColumns()) {
				for (final ColumnData column : table.getStructure().getColumns()) {
					if (!current.contains(this.normalize(column.getLocalName()))) {
						this.addColumn(connection, table, column);
					}
				}
			}

			if (options.isAutoRemoveColumns()) {
				for (final String currentColumn : current) {
					if (!expected.contains(currentColumn)) {
						this.dropColumn(connection, table, currentColumn);
					}
				}
			}
		}
	}

	private void addColumn(final Connection connection, final AbstractDBTable<?> table, final ColumnData column) throws DBException {
		final String columnDefinition = table.getDatabaseEntryUtils().getStructureVisitor().create(table.getStructure(), column);
		final String sql = "ALTER TABLE " + table.getQualifiedName() + " ADD COLUMN " + columnDefinition + ";";
		this.execute(connection, table, sql);
	}

	private Set<String> currentColumns(final Connection connection, final AbstractDBTable<?> table) throws DBException {
		final Set<String> columns = new LinkedHashSet<>();
		try {
			final DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet rs = metaData.getColumns(connection.getCatalog(),
					table.getDatabaseEntryUtils().getStructureVisitor().schemaName(table),
					table.getName(),
					null)) {
				while (rs.next()) {
					columns.add(this.normalize(rs.getString("COLUMN_NAME")));
				}
			}
			return columns;
		} catch (final SQLException e) {
			throw new InternalDBException("Failed to read columns for table " + table.getQualifiedName() + ".",
					null,
					table.getStructure(),
					e);
		}
	}

	private void dropColumn(final Connection connection, final AbstractDBTable<?> table, final String column) throws DBException {
		final String escapedColumnName = table.getDatabaseEntryUtils().getStructureVisitor().qualifiedName(column);
		final String sql = "ALTER TABLE " + table.getQualifiedName() + " DROP COLUMN " + escapedColumnName + ";";
		this.execute(connection, table, sql);
	}

	private void execute(final Connection connection, AbstractDBTable<?> table, final String sql) throws DBException {
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new InternalDBException("Failed to execute schema migration SQL.", sql, table.getStructure(), e);
		}
	}

	private String normalize(final String value) {
		return value == null ? null : value.toLowerCase(Locale.ROOT);
	}

}
