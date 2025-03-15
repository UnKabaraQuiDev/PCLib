package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLBuilder;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKey;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.entry.Reload;
import lu.pcy113.pclib.db.annotations.entry.UniqueKey;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

@GeneratedKey("id")
@UniqueKey("name")
public class Person implements SafeSQLEntry {

	public static UnsafeSQLQuery<Person> byName(String name) {
		return new UnsafeSQLQuery<Person>() {

			@Override
			public String getQuerySQL(SQLQueryable<Person> table) {
				return "SELECT * FROM `" + table.getName() + "` WHERE `name` = '" + name + "';";
			}

			@Override
			public Person clone() {
				return new Person("default");
			}

		};
	}
	
	public static SafeSQLQuery<Person> deleteByName(String name){
		return new SafeSQLQuery<Person>() {

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, name);	
			}
			
			@Override
			public Person clone() {
				return new Person("default");
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<Person> table) {
				return SQLBuilder.safeDelete(table, new String[] {"name"});
			}
		};
	}

	private int id = -1;
	private String name;
	private Date date;
	private String concatted;

	public Person(String name) {
		this.name = name;
	}

	public Person(int id) {
		this.id = id;
	}

	@GeneratedKeyUpdate
	public void smallReload(ResultSet rs) throws SQLException {
		System.err.println("Generated Key Reload: " + rs);

		id = rs.getInt(1);
	}

	@Reload
	public void fullReload(ResultSet rs) throws SQLException {
		System.err.println("fullReload");
		
		id = rs.getInt("id");
		name = rs.getString("name");
		date = rs.getDate("date");
		concatted = rs.getString("concatted");
	}

	@Override
	public <T extends SQLEntry> String getPreparedInsertSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeInsert(table, new String[] { "name" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedUpdateSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeUpdate(table, new String[] { "name", "date" }, new String[] { "id" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedDeleteSQL(DataBaseTable<T> table) {
		return SQLBuilder.safeDelete(table, new String[] { "id" });
	}

	@Override
	public <T extends SQLEntry> String getPreparedSelectSQL(SQLQueryable<T> table) {
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

	public String getConcatted() {
		return concatted;
	}

	@Override
	public String toString() {
		return "Person {id=" + id + ", name=" + name + ", date=" + date + ", concatted=" + concatted + "};";
	}

	@Override
	public Person clone() {
		return new Person(name);
	}

}