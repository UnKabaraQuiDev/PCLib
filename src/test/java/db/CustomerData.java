package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lu.pcy113.pclib.db.annotations.entry.Insert;
import lu.pcy113.pclib.db.annotations.entry.Load;
import lu.pcy113.pclib.db.annotations.entry.Update;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.autobuild.table.Factory;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public class CustomerData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column(length = 64)
	@Unique
	private String name;

	@Column(length = 320)
	@Unique(1)
	private String email;

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
		System.out.println("load");
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

	public static SQLQuery<CustomerData> byNameAndEmail(String name, String email) {
		return new SafeSQLQuery<CustomerData>() {

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
