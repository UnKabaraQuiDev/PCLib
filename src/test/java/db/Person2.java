package db;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLBuilder;
import lu.pcy113.pclib.db.annotations.GeneratedKey;
import lu.pcy113.pclib.db.annotations.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.GeneratedKeyUpdate.Type;
import lu.pcy113.pclib.db.annotations.Reload;
import lu.pcy113.pclib.db.annotations.UniqueKey;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;

@GeneratedKey("id")
@UniqueKey("name")
public class Person2 implements SafeSQLEntry {

	public static UnsafeSQLQuery<Person2> byName(String name) {
		return new UnsafeSQLQuery<Person2>() {

			@Override
			public String getQuerySQL(DataBaseTable<Person2> table) {
				return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = '" + name + "';";
			}

			@Override
			public Person2 clone() {
				return new Person2("default");
			}
		};
	}

	private int id = -1;
	private String name;
	private Date date;

	public Person2(String name) {
		this.name = name;
	}

	public Person2(int id) {
		this.id = id;
	}

	@GeneratedKeyUpdate(type = Type.INDEX, index = 1)
	public void smallReload(BigInteger rs) throws SQLException {
		System.err.println("Generated Key Reload: " + rs);
		
		id = rs.intValue();
	}

	@Reload
	public void fullReload(ResultSet rs) throws SQLException {
		id = rs.getInt("id");
		name = rs.getString("name");
		date = rs.getDate("date");
	}

	@Override
	public <T extends SQLEntry> String getPreparedInsertSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeInsert(table, new String[] { "name" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedUpdateSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeUpdate(table, new String[] { "name", "date" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedDeleteSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeDelete(table, new String[] { "id" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedSelectSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeSelect(table, new String[] { "id" });
	}

	@Override
	public void prepareInsertSQL(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
	}

	@Override
	public void prepareUpdateSQL(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setDate(2, date);
	}

	@Override
	public void prepareDeleteSQL(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, id);
	}

	@Override
	public void prepareSelectSQL(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, id);
	}

	@UniqueKey("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Person2 {id=" + id + ", name=" + name + ", date=" + date + "};";
	}

	@Override
	public Person2 clone() {
		return new Person2(name);
	}

}