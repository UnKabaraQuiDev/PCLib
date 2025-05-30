package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import lu.pcy113.pclib.db.annotations.entry.Insert;
import lu.pcy113.pclib.db.annotations.entry.Load;
import lu.pcy113.pclib.db.annotations.entry.Update;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.autobuild.table.Factory;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SinglePreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.impl.TriFunction;

public class CustomerData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column(length = 64)
	private String name;

	@Column(length = 320)
	@Unique(1)
	private String email;

	@Query(columns = "name")
	public static Function<String, PreparedQuery<CustomerData>> BY_NAME;

	@Query(columns = "name", offset = 1, strategy = Query.Type.FIRST_THROW)
	public static BiFunction<String, Integer, PreparedQuery<CustomerData>> BY_NAME_OFFSET;

	@Query(columns = "email")
	public static Function<String, SinglePreparedQuery<CustomerData>> BY_EMAIL;

	@Query(columns = { "name", "email" })
	public static BiFunction<String, String, PreparedQuery<CustomerData>> BY_NAME_AND_EMAIL;

	@Query(columns = { "name", "email", "age" })
	public static TriFunction<String, String, Integer, PreparedQuery<CustomerData>> BY_NAME_AND_EMAIL_AND_AGE;

	@Query(columns = { "col1", "col2", "col3", "col4" })
	public static Function<Map<String, Object>, PreparedQuery<CustomerData>> BY_OTHERS;

	// @Query(value = "SELECT * FROM customer WHERE id=?;")
	public static Function<Integer, PreparedQuery<CustomerData>> BY_ID_0;

	public CustomerData() {
	}

	public CustomerData(long id) {
		this.id = id;
	}

	public CustomerData(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@Insert
	private void postInsert() {
		System.out.println("insert");
	}

	@Update
	private void postUpdate() {
		System.out.println("update");
	}

	@Load
	private void postLoad() {
		// System.out.println("load");
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CustomerData [id=" + id + ", name=" + name + ", email=" + email + "]";
	}

	@Factory
	public static CustomerData new_() {
		return new CustomerData();
	}

	public static PreparedQuery<CustomerData> byNameAndEmail(String name, String email) {
		return new PreparedQuery<CustomerData>() {

			@Override
			public String getPreparedQuerySQL(SQLQueryable<CustomerData> table) {
				return "SELECT * FROM " + table.getQualifiedName() + " WHERE `name` = ? AND `email` = ?;";
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, name);
				stmt.setString(2, email);
			}

			@Override
			public CustomerData clone() {
				return new CustomerData(-2);
			}

		};
	}

}
