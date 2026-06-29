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
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class DataBaseSchemaMigrator {

	public void migrate(final Connection connection, final Iterable<AbstractDBTable> tables, final SchemaMigrationOptions options)
			throws DBException {
		if (options == null || !options.isAutoAddColumns() && !options.isAutoRemoveColumns()) {
			return;
		}

		for (final AbstractDBTable<?> table : tables) {
			final Set<String> current = this.currentColumns(connection, table);
			final Set<String> expected = Arrays.stream(table.getTableStructure().getColumns())
					.map(ColumnData::getName)
					.map(this::normalize)
					.collect(Collectors.toCollection(LinkedHashSet::new));

			if (options.isAutoAddColumns()) {
				for (final ColumnData column : table.getTableStructure().getColumns()) {
					if (!current.contains(this.normalize(column.getName()))) {
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

	private void addColumn(final Connection connection, final AbstractDBTable<? extends DataBaseEntry> table, final ColumnData column)
			throws DBException {
		final String columnDefinition = table.getDataBaseEntryUtils().getStructureVisitor().create(table.getTableStructure(), column);
		final String sql = "ALTER TABLE " + table.getQualifiedName() + " ADD COLUMN " + columnDefinition + ";";
		this.execute(connection, sql);
	}

	private Set<String> currentColumns(final Connection connection, final AbstractDBTable<? extends DataBaseEntry> table)
			throws DBException {
		final Set<String> columns = new LinkedHashSet<>();
		try {
			final DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet rs = metaData.getColumns(connection.getCatalog(),
					table.getDataBaseEntryUtils().getStructureVisitor().schemaName(table),
					table.getName(),
					null)) {
				while (rs.next()) {
					columns.add(this.normalize(rs.getString("COLUMN_NAME")));
				}
			}
			return columns;
		} catch (final SQLException e) {
			throw new DBException("Failed to read columns for table " + table.getQualifiedName() + ".", e);
		}
	}

	private void dropColumn(final Connection connection, final AbstractDBTable<? extends DataBaseEntry> table, final String column)
			throws DBException {
		final String escapedColumnName = table.getDataBaseEntryUtils().getStructureVisitor().qualifiedName(column);
		final String sql = "ALTER TABLE " + table.getQualifiedName() + " DROP COLUMN " + escapedColumnName + ";";
		this.execute(connection, sql);
	}

	private void execute(final Connection connection, final String sql) throws DBException {
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Failed to execute schema migration SQL: " + sql, e);
		}
	}

	private String normalize(final String value) {
		return value == null ? null : value.toLowerCase(Locale.ROOT);
	}

}
