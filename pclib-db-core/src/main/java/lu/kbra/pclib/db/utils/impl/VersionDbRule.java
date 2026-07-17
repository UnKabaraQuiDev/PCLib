package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class VersionDbRule implements SQLQueryableRule.UpdateRule, SQLQueryableRule.PrepareRule {

	@Override
	public void executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection c, final Object data) {
		final DatabaseEntry entry = (DatabaseEntry) data;

		final String[] columnNames = Arrays.stream(queryable.getStructure().getColumns())
				.filter(col -> col.hasHint(DefaultColumnHints.VERSION_EXPR))
				.map(ColumnData::getLocalQualifiedName)
				.toArray(String[]::new);

		final DatabaseEntryUtils entryUtils = queryable.getDatabaseEntryUtils();
		final SQLStructureVisitor structureVisitor = entryUtils.getStructureVisitor();
		final String sql = structureVisitor.safeSelect(queryable, columnNames, entryUtils.getPrimaryKeyNames(queryable));
		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			entryUtils.prepareSelectSQL(pstmt, queryable, entry);
			System.err.println("Checking version with: " + PCUtils.getStatementAsSQL(pstmt));
			try (final ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					throw new DBException("Row matching primary keys not found.");
				}

				System.err.println(PCUtils.printTree(PCUtils.asMap(rs)));

				for (final ColumnData columnData : queryable.getStructure().getColumns()) {
					if (!columnData.hasHint(DefaultColumnHints.VERSION_EXPR)) {
						continue;
					}

					final Field field = columnData.getField();
					field.setAccessible(true);

					final String columnName = columnData.getLocalName();
					final ColumnType type = columnData.getType();

					final Object remoteValue;
					try {
						remoteValue = type.load(rs, columnName, field.getGenericType());
					} catch (final Exception e) {
						throw new DBException(
								"Failed to decode value/update field for: " + field.getName() + " as " + columnName + " with value '"
										+ rs.getObject(columnName) + "'",
								e);
					}

					final Object localValue;
					try {
						localValue = field.get(entry);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new DBException(
								"Failed to access value from field: " + field.getName() + " as " + columnName + " from "
										+ queryable.getStructure(),
								e);
					}

					System.err.println("remote: " + remoteValue + " local: " + localValue);

					if (!Objects.equals(remoteValue, localValue)) {
						throw new DBException("Version out of sync:\nRemote:" + remoteValue + "\nLocal: " + localValue);
					}
				}

			}
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return SQLQueryableRule.UpdateRule.super.shouldRun(hookType, queryable)
				&& SQLQueryableRule.PrepareRule.super.shouldRun(hookType, queryable)
				&& AbstractDBTable.class.isInstance(queryable.getTargetClass());
	}

}
