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
import java.util.Map;
import java.util.stream.Collectors;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.annotations.table.DB_Table;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.ReadOnlySQLEntry.SafeReadOnlySQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.ReadOnlySQLEntry.UnsafeReadOnlySQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery.SafeTransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery.UnsafeTransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.SQLEntryUtils;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public class DataBaseTable<T extends SQLEntry> implements SQLQueryable<T> {

	private DataBase dataBase;

	private final String tableName;
	private final Column[] columns;
	private final Constraint[] constraints;

	public DataBaseTable(DataBase dbTest) {
		this.dataBase = dbTest;

		DB_Table tableAnnotation = getTypeAnnotation();
		this.tableName = tableAnnotation.name();
		this.columns = tableAnnotation.columns();
		this.constraints = tableAnnotation.constraints();
	}

	public void requestHook(SQLRequestType type, Object query) {
	}

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

	public NextTask<Void, DataBaseTableStatus<T>> create() {
		return exists().thenApply((Boolean status) -> {
			if ((Boolean) status) {
				return new DataBaseTableStatus<T>(true, getQueryable());
			} else {
				Connection con = connect();

				Statement stmt = con.createStatement();

				final String sql = getCreateSQL();

				requestHook(SQLRequestType.CREATE_TABLE, sql);

				stmt.executeUpdate(sql);

				stmt.close();
				return new DataBaseTableStatus<T>(false, getQueryable());
			}
		});
	}

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

	@SuppressWarnings("unused")
	public NextTask<Void, Boolean> exists(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result;

			final Map<String, Object>[] uniques = SQLEntryUtils.getUniqueKeys(getConstraints(), data);

			query: {
				final String safeQuery = SQLBuilder.safeSelectUniqueCollision(getQueryable(), Arrays.stream(uniques).map(unique -> unique.keySet()).collect(Collectors.toList()));

				final PreparedStatement pstmt = con.prepareStatement(safeQuery);

				int i = 1;
				for (Map<String, Object> unique : uniques) {
					for (Object obj : unique.values()) {
						pstmt.setObject(i++, obj);
					}
				}

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying duplicates count.");
			}

			final int count = result.getInt("count");

			stmt.close();
			return count > 0;
		});
	}

	public NextTask<Void, T> insert(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedInsertSQL(getQueryable()), Statement.RETURN_GENERATED_KEYS);

				safeData.prepareInsertSQL(pstmt);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getInsertSQL(getQueryable());

				requestHook(SQLRequestType.INSERT, sql);

				result = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
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

			SQLEntryUtils.generatedKeyUpdate(data, generatedKeys);

			generatedKeys.close();
			stmt.close();
			return data;
		});
	}

	public NextTask<Void, T> insertAndReload(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedInsertSQL(getQueryable()), Statement.RETURN_GENERATED_KEYS);

				safeData.prepareInsertSQL(pstmt);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getInsertSQL(getQueryable());

				requestHook(SQLRequestType.INSERT, sql);

				result = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
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

			SQLEntryUtils.generatedKeyUpdate(data, generatedKeys);

			final PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + getQualifiedName() + " WHERE `" + SQLEntryUtils.getGeneratedKeyName(data) + "` = ?;");
			pstmt.setObject(1, generatedKeys.getObject(1));

			generatedKeys.close();
			stmt.close();

			requestHook(SQLRequestType.SELECT, pstmt);

			final ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				rs.close();
				pstmt.close();
				throw new IllegalStateException("Couldn't query entry after insert.");
			}

			SQLEntryUtils.reload(data, rs);

			rs.close();
			pstmt.close();
			return data;
		});
	}

	public NextTask<Void, T> delete(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedDeleteSQL(getQueryable()));

				safeData.prepareDeleteSQL(pstmt);

				requestHook(SQLRequestType.DELETE, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getDeleteSQL(getQueryable());

				requestHook(SQLRequestType.DELETE, sql);

				result = stmt.executeUpdate(sql);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't delete data.");
			}

			stmt.close();
			return data;
		});
	}

	public NextTask<Void, T> update(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			int result = -1;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedUpdateSQL(getQueryable()));

				safeData.prepareUpdateSQL(pstmt);

				requestHook(SQLRequestType.UPDATE, pstmt);

				result = pstmt.executeUpdate();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getUpdateSQL(getQueryable());

				requestHook(SQLRequestType.UPDATE, sql);

				result = stmt.executeUpdate(sql);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't update data.");
			}

			stmt.close();
			return data;
		});
	}

	public NextTask<Void, T> load(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getQueryable()));

				safeData.prepareSelectSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getSelectSQL(getQueryable());

				requestHook(SQLRequestType.SELECT, sql);

				result = stmt.executeQuery(sql);
			} else if (data instanceof SafeReadOnlySQLEntry) {
				final SafeReadOnlySQLEntry safeData = (SafeReadOnlySQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getQueryable()));

				safeData.prepareSelectSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			} else if (data instanceof UnsafeReadOnlySQLEntry) {
				final UnsafeReadOnlySQLEntry unsafeData = (UnsafeReadOnlySQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getSelectSQL(getQueryable());

				requestHook(SQLRequestType.SELECT, sql);

				result = stmt.executeQuery(sql);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			SQLEntryUtils.reload(data, result);

			result.close();
			stmt.close();

			return data;
		});
	}

	public NextTask<Void, List<T>> query(SQLQuery<T> query) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (query instanceof SafeSQLQuery || query instanceof UnsafeSQLQuery) {
				if (query instanceof SafeSQLQuery) {
					final SafeSQLQuery<T> safeQuery = (SafeSQLQuery<T>) query;

					final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

					safeQuery.updateQuerySQL(pstmt);

					requestHook(SQLRequestType.SELECT, pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (query instanceof UnsafeSQLQuery) {
					final UnsafeSQLQuery<T> unsafeQuery = (UnsafeSQLQuery<T>) query;

					stmt = con.createStatement();

					final String sql = unsafeQuery.getQuerySQL(getQueryable());

					requestHook(SQLRequestType.SELECT, sql);

					result = stmt.executeQuery(sql);
				}

				final List<T> output = new ArrayList<>();
				SQLEntryUtils.copyAll(query, result, output::add);

				stmt.close();
				return output;
			} else if (query instanceof TransformativeSQLQuery) {
				final TransformativeSQLQuery<T> transformativeQuery = (TransformativeSQLQuery<T>) query;

				if (query instanceof SafeTransformativeSQLQuery) {
					final SafeTransformativeSQLQuery<T> safeQuery = (SafeTransformativeSQLQuery<T>) query;

					final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

					safeQuery.updateQuerySQL(pstmt);

					requestHook(SQLRequestType.SELECT, pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (query instanceof UnsafeTransformativeSQLQuery) {
					final UnsafeTransformativeSQLQuery<T> unsafeQuery = (UnsafeTransformativeSQLQuery<T>) query;

					stmt = con.createStatement();

					final String sql = unsafeQuery.getQuerySQL(getQueryable());

					requestHook(SQLRequestType.SELECT, sql);

					result = stmt.executeQuery(sql);
				}

				final List<T> output = transformativeQuery.transform(result);

				stmt.close();
				return output;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		});
	}

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

	public String getCreateSQL() {
		String sql = "CREATE TABLE " + getQualifiedName() + " (\n\t";
		sql += Arrays.stream(columns).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", \n\t"));
		sql += constraints.length > 0 ? ",\n\t" + Arrays.stream(constraints).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", \n\t")) : "";
		sql += "\n) CHARACTER SET " + getCharacterSet() + " COLLATE " + getCollation() + " ENGINE=" + getEngine();
		sql += ";";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return "`" + c.name() + "` " + c.type() + (c.autoIncrement() ? " AUTO_INCREMENT" : "") + (!c.generated() && c.notNull() ? " NOT NULL" : "") + (!c.default_().equals("") ? " DEFAULT " + c.default_() : "")
				+ (!c.onUpdate().equals("") ? " ON UPDATE " + c.onUpdate() : "") + (c.generated() ? " GENERATED ALWAYS AS (" + c.generator() + ") " + c.generatedType().name() : "");
	}

	protected String getCreateSQL(Constraint c) {
		if (c.type().equals(Constraint.Type.FOREIGN_KEY)) {
			if (c.columns().length > 1) {
				throw new IllegalArgumentException("Foreign key constraint only applies to 1 columns (" + c.name() + ", " + Arrays.toString(c.columns()) + ")");
			}
			return "CONSTRAINT " + c.name() + " FOREIGN KEY (" + c.columns()[0] + ") REFERENCES `" + c.referenceTable() + "` (`" + c.referenceColumn() + "`) ON DELETE " + c.onDelete() + " ON UPDATE " + c.onUpdate();
		} else if (c.type().equals(Constraint.Type.UNIQUE)) {
			return "CONSTRAINT " + c.name() + " UNIQUE (" + (Arrays.stream(c.columns()).collect(Collectors.joining("`, `", "`", "`"))) + ")";
		} else if (c.type().equals(Constraint.Type.CHECK)) {
			return "CONSTRAINT " + c.name() + " CHECK (" + c.check() + ")";
		} else if (c.type().equals(Constraint.Type.PRIMARY_KEY)) {
			return "CONSTRAINT " + c.name() + " PRIMARY KEY (" + (Arrays.stream(c.columns()).collect(Collectors.joining("`, `", "`", "`"))) + ")";
		} else if (c.type().equals(Constraint.Type.INDEX)) {
			return "INDEX " + c.name() + " (" + (Arrays.stream(c.columns()).collect(Collectors.joining("`, `", "`", "`"))) + ")";
		} else {
			throw new IllegalArgumentException(c + ", is not defined");
		}
	}

	protected DataBaseTable<T> getQueryable() {
		return this;
	}

	@Override
	public String getName() {
		return tableName;
	}

	@Override
	public String getQualifiedName() {
		return "`" + dataBase.getDataBaseName() + "`.`" + getName() + "`";
	}

	public Column[] getColumns() {
		return columns;
	}

	public String getCharacterSet() {
		return getTypeAnnotation().characterSet().equals("") ? dataBase.getConnector().getCharacterSet() : getTypeAnnotation().characterSet();
	}

	public String getCollation() {
		return getTypeAnnotation().collation().equals("") ? dataBase.getConnector().getCollation() : getTypeAnnotation().collation();
	}

	public String getEngine() {
		return getTypeAnnotation().engine().equals("") ? dataBase.getConnector().getEngine() : getTypeAnnotation().engine();
	}

	public Constraint[] getConstraints() {
		return constraints;
	}

	public String[] getColumnNames() {
		return Arrays.stream(columns).map((c) -> c.name()).toArray(String[]::new);
	}

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	protected Connection createConnection() throws SQLException {
		return dataBase.getConnector().createConnection();
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	private DB_Table getTypeAnnotation() {
		return getClass().getAnnotation(DB_Table.class);
	}

	@Override
	public String toString() {
		return "DataBaseTable{" + "tableName='" + getQualifiedName() + "'" + '}';
	}

	public static class DataBaseTableStatus<T extends SQLEntry> {
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
