package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.builder.SQLBuilder;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;
import lu.pcy113.pclib.db.autobuild.table.ConstraintData;
import lu.pcy113.pclib.db.autobuild.table.TableName;
import lu.pcy113.pclib.db.autobuild.table.TableStructure;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.impl.SQLTypeAnnotated;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public class DataBaseTable<T extends DataBaseEntry> implements AbstractDBTable<T>, SQLTypeAnnotated<TableName> {

	private DataBase dataBase;
	private DataBaseEntryUtils dbEntryUtils;
	private TableStructure structure;
	private Class<? extends AbstractDBTable<T>> tableClass;

	public DataBaseTable(DataBase dataBase) {
		this(dataBase, new BaseDataBaseEntryUtils());
	}

	public DataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = (Class<? extends AbstractDBTable<T>>) getClass();

		gen();
	}

	public DataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = tableClass;

		gen();
	}

	protected void gen() {
		structure = dbEntryUtils.scanTable((Class<? extends DataBaseTable<T>>) this.getClass());
		structure.update(dataBase.getConnector());
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
		System.err.println(query);
	}

	@Override
	public NextTask<Void, Boolean> exists() {
		return NextTask.create(() -> {
			final Connection con = connect();

			DatabaseMetaData dbMetaData = con.getMetaData();
			ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getName(), null);

			if (rs.next()) {
				rs.close();

				return true;
			} else {
				rs.close();

				return false;
			}
		});
	}

	@Override
	public NextTask<Void, DataBaseTableStatus<T>> create() {
		return exists().thenApply((Boolean status) -> {
			if ((Boolean) status) {
				return new DataBaseTableStatus<T>(true, getQueryable());
			} else {
				Connection con = connect();

				Statement stmt = con.createStatement();

				final String sql = getCreateSQL();

				requestHook(SQLRequestType.CREATE_TABLE, sql);

				int result = stmt.executeUpdate(sql);

				stmt.close();
				return new DataBaseTableStatus<T>(false, getQueryable());
			}
		});
	}

	@Override
	public NextTask<Void, DataBaseTable<T>> drop() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();

			final String sql = "DROP TABLE " + getQualifiedName() + ";";

			requestHook(SQLRequestType.DROP_TABLE, sql);

			stmt.executeUpdate(sql);

			stmt.close();

			return getQueryable();
		});
	}

	@Override
	public NextTask<Void, Integer> count(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result;

			final List<String>[] uniqueKeys = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			query: {
				final PreparedStatement pstmt = con
						.prepareStatement(dbEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				dbEntryUtils.prepareSelectCountUniqueSQL(pstmt, uniqueKeys, data);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying duplicates count.");
			}

			final int count = result.getInt("count");

			stmt.close();
			return count;
		});
	}

	@Override
	public NextTask<Void, Boolean> exists(T data) {
		return count(data).thenApply(count -> count > 0);
	}

	@Override
	public NextTask<Void, Boolean> existsUnique(T data) {
		return count(data).thenApply(count -> count != 0);
	}

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	@Override
	public NextTask<Void, T> loadIfExists(T data) {
		return count(data).thenCompose(count -> {
			if (count == 1) {
				return loadUnique(data);
			} else if (count == 0) {
				return NextTask.create(() -> null);
			} else {
				throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
			}
		});
	}

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	@Override
	public NextTask<Void, T> loadIfExistsElseInsert(T data) {
		return count(data).thenCompose(count -> {
			if (count == 1) {
				return loadUnique(data);
			} else if (count == 0) {
				return insertAndReload(data);
			} else {
				throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
			}
		});
	}

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	@Override
	public NextTask<Void, T> loadUnique(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result;

			final List<String>[] uniqueKeys = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			query: {
				final PreparedStatement pstmt = con
						.prepareStatement(dbEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				dbEntryUtils.prepareSelectUniqueSQL(pstmt, uniqueKeys, data);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying by uniques.");
			}

			dbEntryUtils.fillLoad(data, result);

			stmt.close();
			return data;
		});
	}

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	@Override
	public NextTask<Void, List<T>> loadByUnique(T data) {
		return NextTask.create(() -> {
			final List<String>[] uniques = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			return new PreparedQuery<T>() {

				@Override
				public String getPreparedQuerySQL(SQLQueryable<T> table) {
					return dbEntryUtils.getPreparedSelectUniqueSQL(getQueryable(), uniques, data);
				}

				@Override
				public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
					dbEntryUtils.prepareSelectUniqueSQL(stmt, uniques, data);
				}

				@Override
				public T clone() {
					return dbEntryUtils.instance(data);
				}

			};
		}).thenCompose(this::query);
	}

	@Override
	public NextTask<Void, T> insert(T data) {
		return NextTask.create(() -> {
			if (data instanceof ReadOnlyDataBaseEntry) {
				throw new IllegalStateException("Cannot insert a read-only entry (" + data.getClass().getName() + ").");
			}

			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				final PreparedStatement pstmt = con.prepareStatement(dbEntryUtils.getPreparedInsertSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareInsertSQL(pstmt, data);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't insert data.");
			}

			final ResultSet generatedKeys = stmt.getGeneratedKeys();
			if (!generatedKeys.next()) {
				generatedKeys.close();
				stmt.close();
				throw new IllegalStateException("Couldn't get generated keys after insert.");
			}

			dbEntryUtils.fillInsert(data, generatedKeys);

			generatedKeys.close();
			stmt.close();
			return data;
		});
	}

	@Override
	public NextTask<Void, T> insertAndReload(T data) {
		return insert(data).thenCompose(this::load);
	}

	@Override
	public NextTask<Void, T> delete(T data) {
		return NextTask.create(() -> {
			if (data instanceof ReadOnlyDataBaseEntry) {
				throw new IllegalStateException("Cannot delete a read-only entry (" + data.getClass().getName() + ").");
			}

			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				final PreparedStatement pstmt = con.prepareStatement(dbEntryUtils.getPreparedDeleteSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareDeleteSQL(pstmt, data);

				requestHook(SQLRequestType.DELETE, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't delete data.");
			}

			stmt.close();
			return data;
		});
	}

	@Override
	public NextTask<Void, T> deleteIfExists(T data) {
		return exists(data).thenCompose(b -> b ? delete(data) : NextTask.empty());
	}

	@Override
	public NextTask<Void, T> deleteUnique(T data) {
		return exists(data).thenCompose(e -> e ? loadUnique(data).thenCompose(b -> delete(data)) : NextTask.empty());
	}

	@Override
	public NextTask<Void, List<T>> deleteByUnique(T data) {
		return exists(data)
				.thenCompose(e -> e ? loadByUnique(data).thenParallel(l -> l.forEach(el -> delete(el).run())) : NextTask.empty());
	}

	@Override
	public NextTask<Void, T> update(T data) {
		return NextTask.create(() -> {
			if (data instanceof ReadOnlyDataBaseEntry) {
				throw new IllegalStateException("Cannot update a read-only entry (" + data.getClass().getName() + ").");
			}

			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				final PreparedStatement pstmt = con.prepareStatement(dbEntryUtils.getPreparedUpdateSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareUpdateSQL(pstmt, data);

				requestHook(SQLRequestType.UPDATE, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't update data.");
			}

			stmt.close();
			return data;
		});
	}

	@Override
	public NextTask<Void, T> updateAndReload(T data) {
		return update(data).thenCompose(this::load);
	}

	@Override
	public NextTask<Void, T> load(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				final PreparedStatement pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareSelectSQL(pstmt, data);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			dbEntryUtils.fillLoad(data, result);

			result.close();
			stmt.close();

			return data;
		});
	}

	@Override
	public <B> NextTask<Void, B> query(SQLQuery<T, B> query) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

				safeQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable((Class<? extends SQLQueryable<T>>) getQueryable().getClass(), query, result, output::add);

				stmt.close();
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final B output = safeTransQuery.transform(result);

				stmt.close();
				return output;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable((Class<? extends SQLQueryable<T>>) getQueryable().getClass(), query, result, output::add);

				final B filteredOutput = safeTransQuery.transform(output);

				stmt.close();
				return filteredOutput;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		});
	}

	@Override
	public NextTask<Void, Integer> count() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();
			ResultSet result;

			final String sql = SQLBuilder.count(getQueryable());

			requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			final int count = result.getInt("count");

			result.close();
			stmt.close();
			return count;
		});
	}

	@Override
	public NextTask<Void, Integer> clear() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();

			final String sql = "DELETE FROM " + getQualifiedName() + ";";

			requestHook(SQLRequestType.DELETE, sql);

			int result = stmt.executeUpdate(sql);

			stmt.close();
			return result;
		});
	}

	@Override
	public NextTask<Void, Integer> truncate() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();

			final String sql = "TRUNCATE TABLE " + getQualifiedName() + ";";

			requestHook(SQLRequestType.TRUNCATE, sql);

			int result = stmt.executeUpdate(sql);

			stmt.close();
			return result;
		});
	}

	@Override
	public String getCreateSQL() {
		return structure.build();
	}

	private String escape(String column) {
		if (column == null) {
			throw new IllegalArgumentException("Column name cannot be null.");
		}
		return PCUtils.sqlEscapeIdentifier(column);
	}

	protected DataBaseTable<T> getQueryable() {
		return this;
	}

	@Override
	public String getName() {
		return structure.getName();
	}

	@Override
	public String getQualifiedName() {
		return "`" + dataBase.getDataBaseName() + "`.`" + getName() + "`";
	}

	@Override
	public TableName getTypeAnnotation() {
		return tableClass.getAnnotation(TableName.class);
	}

	public ColumnData[] getColumns() {
		return structure.getColumns();
	}

	public String getCharacterSet() {
		return structure.getCharacterSet().equals("") ? dataBase.getConnector().getCharacterSet() : structure.getCharacterSet();
	}

	public String getCollation() {
		return structure.getCollation().equals("") ? dataBase.getConnector().getCollation() : structure.getCollation();
	}

	public String getEngine() {
		return structure.getEngine().equals("") ? dataBase.getConnector().getEngine() : structure.getEngine();
	}

	public ConstraintData[] getConstraints() {
		return structure.getConstraints();
	}

	public String[] getColumnNames() {
		return Arrays.stream(structure.getColumns()).map((c) -> c.getName()).toArray(String[]::new);
	}

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	protected Connection createConnection() throws SQLException {
		return dataBase.getConnector().createConnection();
	}

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return dbEntryUtils;
	}

	public void setDbEntryUtils(DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	@Override
	public String toString() {
		return "DataBaseTable{" + "tableName='" + getQualifiedName() + "'" + '}';
	}

	public static class DataBaseTableStatus<T extends DataBaseEntry> {
		private boolean existed;
		private DataBaseTable<T> table;

		protected DataBaseTableStatus(boolean existed, DataBaseTable<T> table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public DataBaseTable<T> getQueryable() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseTableStatus{existed=" + existed + ", created=" + !existed + ", table=" + table + "}";
		}

	}

}
