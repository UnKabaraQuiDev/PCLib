package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.GeneratedKey;
import lu.pcy113.pclib.db.annotations.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.Reload;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;

@GeneratedKey("id")
public class Person implements UnsafeSQLEntry, SafeSQLEntry {

	public static UnsafeSQLQuery<Person> byName(String name) {
		return new UnsafeSQLQuery<Person>() {

			@Override
			public String getSelectSQL(DataBaseTable<Person> table) {
				return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = " + name + ";";
			}

			@Override
			public String getQuerySQL(DataBaseTable<Person> table) {
				return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = '" + name + "';";
			}

			@Override
			public Person clone() {
				return new Person("default");
			}
		};
	}
	
	private int id = -1;
	private String name;
	private Date date;

	public Person(String name) {
		this.name = name;
	}

	@GeneratedKeyUpdate
	public void smallReload(ResultSet rs) throws SQLException {
		id = rs.getInt(1);
	}

	@Reload
	public void fullReload(ResultSet rs) throws SQLException {
		id = rs.getInt("id");
		name = rs.getString("name");
		date = rs.getDate("date");
	}

	@Override
	public <T extends SQLEntry> String getInsertSQL(DataBaseTable<T> table) {
		return "INSERT INTO `" + table.getTableName() + "`(name) VALUES ('" + name + "');";
	}

	@Override
	public <T extends SQLEntry> String getUpdateSQL(DataBaseTable<T> table) {
		return "UPDATE `" + table.getTableName() + "` SET `name` = '" + name + "', `date` = " + date + " WHERE `id` = " + id + ";";
	}

	@Override
	public <T extends SQLEntry> String getDeleteSQL(DataBaseTable<T> table) {
		return "DELETE FROM `" + table.getTableName() + "` WHERE `id` = " + id + ";";
	}

	@Override
	public <T extends SQLEntry> String getSelectSQL(DataBaseTable<T> table) {
		return "SELECT * FROM `" + table.getTableName() + "` WHERE `id` = " + id + ";";
	}

	@Override
	public <T extends SQLEntry> String getQuerySQL(DataBaseTable<T> table) {
		return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = '" + name + "';";
	}

	@Override
	public <T extends SQLEntry> String getPreparedInsertSQL(DataBaseTable<T> table) {
		return "INSERT INTO `" + table.getTableName() + "`(`name`) VALUES (?);";
	}

	@Override
	public <T extends SQLEntry> String getPreparedUpdateSQL(DataBaseTable<T> table) {
		return "UPDATE `" + table.getTableName() + "` SET `name` = ?, `date` = ? WHERE `id` = ?;";
	}

	@Override
	public <T extends SQLEntry> String getPreparedDeleteSQL(DataBaseTable<T> table) {
		return "DELETE FROM `" + table.getTableName() + "` WHERE `id` = ?;";
	}

	@Override
	public <T extends SQLEntry> String getPreparedQuerySQL(DataBaseTable<T> table) {
		return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = ?;";
	}

	@Override
	public <T extends SQLEntry> String getPreparedSelectSQL(DataBaseTable<T> table) {
		if (id == -1) {
			return "SELECT * FROM `" + table.getTableName() + "` WHERE `name` = ?;";
		} else {
			return "SELECT * FROM `" + table.getTableName() + "` WHERE `id` = ?;";
		}
	}

	@Override
	public void prepareInsertSQL(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
	}

	@Override
	public void prepareUpdateSQL(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setDate(2, date);
		stmt.setInt(3, id);
	}

	@Override
	public void prepareDeleteSQL(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, id);
	}

	@Override
	public void prepareSelectSQL(PreparedStatement stmt) throws SQLException {
		if (id == -1) {
			stmt.setString(1, name);
		} else {
			stmt.setInt(1, id);
		}
	}

	@Override
	public void prepareQuerySQL(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
	}

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
		return "Person {id=" + id + ", name=" + name + ", date=" + date + "};";
	}

	@Override
	public Person clone() {
		return new Person(name);
	}

}