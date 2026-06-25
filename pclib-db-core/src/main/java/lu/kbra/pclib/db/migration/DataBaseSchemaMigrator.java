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

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.query.SQLQueryVisitors;
import lu.kbra.pclib.db.table.DataBaseTable;

public class DataBaseSchemaMigrator {

	private final DataBaseConnector connector;

	public DataBaseSchemaMigrator(final DataBaseConnector connector) {
		this.connector = connector;
	}

	private void addColumn(final Connection connection, final DataBaseTable<? extends DataBaseEntry> table, final ColumnData column)
			throws DBException {
		final String columnDefinition = SQLStructureVisitors.forConnector(this.connector).visit(table.getTableStructure(), column);
		final String sql = "ALTER TABLE " + table.getQualifiedName() + " ADD COLUMN " + columnDefinition + ";";
		this.execute(connection, sql);
	}

	private String catalog(final DataBaseTable<? extends DataBaseEntry> table) {
		final String protocol = this.connector.getProtocol();
		if ("mysql".equalsIgnoreCase(protocol)) {
			return table.getDataBase().getDataBaseName();
		}
		return null;
	}

	private Set<String> currentColumns(final Connection connection, final DataBaseTable<? extends DataBaseEntry> table) throws DBException {
		final Set<String> columns = new LinkedHashSet<>();
		try {
			final DatabaseMetaData metaData = connection.getMetaData();
			final String catalog = this.catalog(table);
			final String schema = SQLQueryVisitors.forConnector(this.connector).schemaName(table);
			try (ResultSet rs = metaData.getColumns(catalog, schema, table.getName(), null)) {
				while (rs.next()) {
					columns.add(this.normalize(rs.getString("COLUMN_NAME")));
				}
			}
			return columns;
		} catch (final SQLException e) {
			throw new DBException("Failed to read columns for table " + table.getQualifiedName() + ".", e);
		}
	}

	private void dropColumn(final Connection connection, final DataBaseTable<? extends DataBaseEntry> table, final String column)
			throws DBException {
		final String escapedColumnName = SQLQueryVisitors.forConnector(this.connector).qualifiedName(column);
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

	public void migrate(
			final Connection connection,
			final Iterable<? extends DataBaseTable<? extends DataBaseEntry>> tables,
			final SchemaMigrationOptions options)
			throws DBException {
		if (options == null || !options.isAutoAddColumns() && !options.isAutoRemoveColumns()) {
			return;
		}

		for (final DataBaseTable<? extends DataBaseEntry> table : tables) {
			final Set<String> current = this.currentColumns(connection, table);
			final Set<String> expected = Arrays.stream(table.getColumns())
					.map(ColumnData::getName)
					.map(this::normalize)
					.collect(Collectors.toCollection(LinkedHashSet::new));

			if (options.isAutoAddColumns()) {
				for (final ColumnData column : table.getColumns()) {
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

	private String normalize(final String value) {
		return value == null ? null : value.toLowerCase(Locale.ROOT);
	}

}
