package db2.datas;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKey;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.entry.Reload;
import lu.pcy113.pclib.db.annotations.entry.UniqueKey;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

@GeneratedKey("id")
public class CustomerData implements UnsafeSQLEntry {

	private int id;
	private String name;
	private Timestamp registerTime;

	public CustomerData() {
	}

	public CustomerData(String name) {
		this.name = name;
	}

	public CustomerData(String name, Timestamp registerTime) {
		this.name = name;
		this.registerTime = registerTime;
	}

	@GeneratedKeyUpdate(type = GeneratedKeyUpdate.Type.INDEX)
	public void generatedKeyUpdate(BigInteger bigInt) {
		this.id = bigInt.intValue();
	}

	@Reload
	public void reload(ResultSet rs) throws SQLException {
		this.id = rs.getInt("id");
		this.name = rs.getString("name");
		this.registerTime = rs.getTimestamp("register_time");
	}

	@Override
	public <T extends SQLEntry> String getInsertSQL(DataBaseTable<T> table) {
		if (registerTime == null) {
			return "INSERT INTO " + table.getQualifiedName() + "(`name`) VALUES ('" + name + "');";
		} else {
			return "INSERT INTO " + table.getQualifiedName() + "(`name`, `register_time`) VALUES ('" + name + "', '" + registerTime + "');";
		}
	}

	@Override
	public <T extends SQLEntry> String getUpdateSQL(DataBaseTable<T> table) {
		return null;
	}

	@Override
	public <T extends SQLEntry> String getDeleteSQL(DataBaseTable<T> table) {
		return null;
	}

	@Override
	public <T extends SQLEntry> String getSelectSQL(SQLQueryable<T> table) {
		return "SELECT * FROM " + table.getQualifiedName() + " WHERE `id` = " + this.id + ";";
	}

	public int getId() {
		return id;
	}

	@UniqueKey("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getRegisterTime() {
		return registerTime;
	}

	@Override
	public CustomerData clone() {
		return new CustomerData();
	}

	@Override
	public String toString() {
		return "CustomerData [id=" + id + ", name=" + name + ", registerTime=" + registerTime + "]";
	}

	public static UnsafeSQLQuery<CustomerData> byName(String name) {
		return new UnsafeSQLQuery<CustomerData>() {

			@Override
			public String getQuerySQL(SQLQueryable<CustomerData> table) {
				return "SELECT * FROM " + table.getQualifiedName() + " WHERE `name` = '" + name + "';";
			}

			@Override
			public CustomerData clone() {
				return new CustomerData();
			}

		};
	}
}
