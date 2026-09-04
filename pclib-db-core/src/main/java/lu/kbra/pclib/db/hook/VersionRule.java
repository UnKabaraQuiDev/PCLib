package lu.kbra.pclib.db.hook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
import lu.kbra.pclib.db.domain.dialect.LockMode;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.exception.VersionConflictException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.ArrayObject;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.AfterRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.ErrorRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.UpdateRule;
import lu.kbra.pclib.db.utils.impl.StorageBinding;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VersionRule implements PrepareRule, AfterRule, ErrorRule, UpdateRule {

	private static final String PREV_AUTO_COMMIT = "PREV_AUTO_COMMIT";

	private boolean runOnBatches = false;

	private final Map<TableStructure, VersionColumns> cache = new ConcurrentHashMap<>();

	@Override
	public void
			executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final AbstractConnection c, final Object data) {
		if (!(data instanceof DatabaseEntry) && !(this.runOnBatches && data instanceof Collection<?>)) {
			return;
		}

		final boolean previousAutoCommit;
		try {
			previousAutoCommit = c.getAutoCommit();

			if (previousAutoCommit) {
				c.setAutoCommit(false);
			}

			c.setAttribute(VersionRule.PREV_AUTO_COMMIT, previousAutoCommit);
		} catch (final SQLException e) {
			throw new InternalDBException("Exception while setting auto-commit to: false", null, queryable.getStructure(), e);
		}

		if (this.runOnBatches && data instanceof Collection<?>) {
			this.checkBatch(hookType, queryable, c, (Collection<?>) data);
		} else {
			this.check(hookType, queryable, c, (DatabaseEntry) data);
		}
	}

	@Override
	public void executeAfter(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement stmt,
			final Object data) {
		if (!c.hasAttribute(VersionRule.PREV_AUTO_COMMIT)) {
			return;
		}

		final boolean previousAutoCommit = c.getAttribute(VersionRule.PREV_AUTO_COMMIT);

		try {
			this.finishTransaction(c, previousAutoCommit, true);
		} catch (final SQLException e) {
			throw new InternalDBException("Exception while committing.", null, queryable.getStructure(), e);
		}
	}

	@Override
	public void executeError(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Throwable t,
			final Object data)
			throws Throwable {
		if (!c.hasAttribute(VersionRule.PREV_AUTO_COMMIT)) {
			return;
		}

		final boolean previousAutoCommit = c.getAttribute(VersionRule.PREV_AUTO_COMMIT);

		try {
			this.finishTransaction(c, previousAutoCommit, false);
		} catch (final SQLException e) {
			throw e;
		}
	}

	private void finishTransaction(final AbstractConnection c, final boolean previousAutoCommit, final boolean success)
			throws SQLException {
		if (!previousAutoCommit) {
			return;
		}

		if (success) {
			c.commit();
		} else {
			c.rollback();
		}

		c.setAutoCommit(true);
		c.removeAttribute(VersionRule.PREV_AUTO_COMMIT);
	}

	protected void checkBatch(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final Connection connection,
			final Collection<?> data) {

		if (data.isEmpty()) {
			return;
		}

		final VersionColumns versionColumns = this.cache.computeIfAbsent((TableStructure) queryable.getStructure(),
				this::createVersionColumns);

		if (versionColumns.columns.length == 0) {
			return;
		}

		final DatabaseEntryUtils entryUtils = queryable.getDatabaseEntryUtils();
		final SQLStructureVisitor structureVisitor = entryUtils.getStructureVisitor();

		final ColumnData[] primaryKeys = entryUtils.getPrimaryKeys(queryable);
		final int pkCount = primaryKeys.length;
		final Map<ArrayObject<Object>, DatabaseEntry> pkMap = new HashMap<>(data.size());

		for (final Object object : data) {
			final DatabaseEntry entry = (DatabaseEntry) object;
			final Object[] pkValues = entryUtils.getPrimaryKeyValues(queryable, entry);
			final ArrayObject<Object> key = new ArrayObject<>(pkValues);
			if (pkMap.put(key, entry) != null) {
				throw new IllegalArgumentException("Duplicate primary key in batch: " + key);
			}
		}

		final String[] selectColumns = Stream
				.concat(Arrays.stream(primaryKeys).map(ColumnData::getLocalQualifiedName), Arrays.stream(versionColumns.columnNames))
				.toArray(String[]::new);

		final String sql = structureVisitor.safeSelect(queryable,
				selectColumns,
				entryUtils.getPrimaryKeyNames(queryable),
				structureVisitor.supports(DbmsCapability.SELECT_FOR_UPDATE_LOCKING) ? LockMode.FOR_UPDATE : LockMode.NONE,
				data.size());

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			int index = 1;

			for (final DatabaseEntry entry : pkMap.values()) {
				final Object[] pkValues = entryUtils.getPrimaryKeyValues(queryable, entry);

				for (int i = 0; i < pkCount; i++) {
					primaryKeys[i].getType().store(statement, index, pkValues[i]);
					index += primaryKeys[i].getType().storeLength(statement, index, pkValues[i]);
				}
			}

			try (ResultSet rs = statement.executeQuery()) {

				final Set<ArrayObject<Object>> found = new HashSet<>(pkMap.size());

				while (rs.next()) {

					final Object[] pkValues = new Object[pkCount];

					for (int i = 0; i < pkCount; i++) {
						pkValues[i] = primaryKeys[i].getType().load(rs, i + 1, primaryKeys[i].getStorageBinding().getGenericType());
					}

					final ArrayObject<Object> pk = new ArrayObject<>(pkValues);
					final DatabaseEntry entry = pkMap.get(pk);

					if (entry == null) {
						continue;
					}
					found.add(pk);

					this.checkVersions(rs, versionColumns, entry, pkCount);
				}

				if (found.size() != pkMap.size()) {
					throw new NoMatchingRowException("Not all rows matching the primary keys were found.", sql, queryable.getStructure());
				}
			}
		} catch (final SQLException e) {
			throw new InternalDBException(queryable.getStructure(), e);
		}
	}

	protected void
			checkVersions(final ResultSet rs, final VersionColumns versionColumns, final DatabaseEntry entry, final int resultColumnOffset)
					throws SQLException {
		for (int i = 0; i < versionColumns.columns.length; i++) {
			final ColumnData columnData = versionColumns.columns[i];
			final StorageBinding storageBinding = columnData.getStorageBinding();
			final ColumnType<?, ?> type = columnData.getType();

			final Object remoteValue = type.load(rs, resultColumnOffset + i + 1, storageBinding.getGenericType());
			final Object localValue = storageBinding.get(entry);

			if (!Objects.equals(remoteValue, localValue)) {
				throw new VersionConflictException("Version out of sync:\n" + "Remote: " + remoteValue + "\nLocal: " + localValue);
			}
		}
	}

	protected void
			check(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection connection, final DatabaseEntry entry) {

		final VersionColumns versionColumns = this.cache.computeIfAbsent((TableStructure) queryable.getStructure(),
				this::createVersionColumns);

		if (versionColumns.columns.length == 0) {
			return;
		}

		final DatabaseEntryUtils entryUtils = queryable.getDatabaseEntryUtils();
		final SQLStructureVisitor structureVisitor = entryUtils.getStructureVisitor();

		final String sql = structureVisitor.safeSelect(queryable,
				versionColumns.columnNames,
				entryUtils.getPrimaryKeyNames(queryable),
				structureVisitor.supports(DbmsCapability.SELECT_FOR_UPDATE_LOCKING) ? LockMode.FOR_UPDATE : LockMode.NONE);

		try (PreparedStatement statement = connection.prepareStatement(sql)) {

			entryUtils.prepareSelectSQL(statement, queryable, entry);

			try (ResultSet rs = statement.executeQuery()) {
				if (!rs.next()) {
					throw new NoMatchingRowException("No rows matching the primary keys found.", sql, queryable.getStructure());
				}

				this.checkVersions(rs, versionColumns, entry, 0);
			}
		} catch (final SQLException e) {
			throw new InternalDBException(queryable.getStructure(), e);
		}
	}

	private VersionColumns createVersionColumns(final TableStructure structure) {
		final ColumnData[] columns = Arrays.stream(structure.getColumns())
				.filter(column -> column.getBooleanHint(DefaultColumnHints.VERSION))
				.toArray(ColumnData[]::new);

		final String[] columnNames = Arrays.stream(columns).map(ColumnData::getLocalQualifiedName).toArray(String[]::new);

		return new VersionColumns(columns, columnNames);
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor
	private static final class VersionColumns {

		private final ColumnData[] columns;
		private final String[] columnNames;

	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return SQLQueryableRule.UpdateRule.super.shouldRun(hookType, queryable)
				&& (SQLQueryableRule.PrepareRule.super.shouldRun(hookType, queryable)
						|| SQLQueryableRule.AfterRule.super.shouldRun(hookType, queryable)
						|| SQLQueryableRule.ErrorRule.super.shouldRun(hookType, queryable))
				&& AbstractDBTable.class.isAssignableFrom(queryable.getTargetClass());
	}

}
