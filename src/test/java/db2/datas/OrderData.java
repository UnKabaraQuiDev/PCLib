package db2.datas;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKey;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.entry.Reload;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

@GeneratedKey("id")
public class OrderData implements UnsafeSQLEntry {

	private int id, customerId;
	private String description;
	private int price;
	private boolean delivered;

	public OrderData() {
	}

	public OrderData(CustomerData cd, String description, int price, boolean delivered) {
		this(cd.getId(), description, price, delivered);
	}

	public OrderData(int customerId, String description, int price, boolean delivered) {
		this.customerId = customerId;
		this.description = description;
		this.price = price;
		this.delivered = delivered;
	}

	@GeneratedKeyUpdate(type = GeneratedKeyUpdate.Type.INDEX)
	public void generatedKeyUpdate(BigInteger bigInt) {
		this.id = bigInt.intValue();
	}

	@Reload
	public void reload(ResultSet rs) throws SQLException {
		this.id = rs.getInt("id");
		this.customerId = rs.getInt("customer_id");
		this.description = rs.getString("description");
		this.price = rs.getInt("price");
		this.delivered = rs.getBoolean("delivered");
	}

	@Override
	public <T extends SQLEntry> String getInsertSQL(DataBaseTable<T> table) {
		return "INSERT INTO " + table.getQualifiedName() + "(`customer_id`, `description`, `price`, `delivered`) VALUES (" + customerId + ", '" + description + "', " + price + ", " + (delivered ? 0x01 : 0x00) + ");";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public int getId() {
		return id;
	}

	public int getCustomerId() {
		return customerId;
	}

	@Override
	public OrderData clone() {
		return new OrderData();
	}

	@Override
	public String toString() {
		return "OrderData [id=" + id + ", customerId=" + customerId + ", description=" + description + ", price=" + price + ", delivered=" + delivered + "]";
	}

	public static UnsafeSQLQuery<OrderData> byCustomer(CustomerData cd) {
		return byCustomer(cd.getId());
	}

	public static UnsafeSQLQuery<OrderData> byCustomer(int customerId) {
		return new UnsafeSQLQuery<OrderData>() {

			@Override
			public String getQuerySQL(SQLQueryable<OrderData> table) {
				return "SELECT * FROM " + table.getQualifiedName() + " WHERE `customer_id` = " + customerId + ";";
			}

			@Override
			public OrderData clone() {
				return new OrderData();
			}

		};
	}

	public static UnsafeSQLQuery<OrderData> byCustomerAndDescription(CustomerData cd, String desc) {
		return byCustomerAndDescription(cd.getId(), desc);
	}

	public static UnsafeSQLQuery<OrderData> byCustomerAndDescription(int customerId, String desc) {
		return new UnsafeSQLQuery<OrderData>() {

			@Override
			public String getQuerySQL(SQLQueryable<OrderData> table) {
				return "SELECT * FROM " + table.getQualifiedName() + " WHERE `customer_id` = " + customerId + " AND `description` = '" + desc + "';";
			}

			@Override
			public OrderData clone() {
				return new OrderData();
			}

		};
	}

}
