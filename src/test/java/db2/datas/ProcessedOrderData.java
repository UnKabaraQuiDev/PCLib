package db2.datas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.pclib.db.impl.SQLEntry.ReadOnlySQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery.SafeTransformativeSQLQuery;
import lu.pcy113.pclib.db.utils.SQLBuilder;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public class ProcessedOrderData implements ReadOnlySQLEntry {

	private int orderId, customerId;
	private String description;
	private int price;
	private String customerName;

	private boolean delivered = false;

	public ProcessedOrderData(boolean delivered) {
		this.delivered = delivered;
	}

	public ProcessedOrderData(int orderId, int customerId, String description, int price, String customerName) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.description = description;
		this.price = price;
		this.customerName = customerName;
	}

	public static SQLQuery<ProcessedOrderData> query(boolean delivered) {
		return new SafeTransformativeSQLQuery<ProcessedOrderData>() {

			@Override
			public List<ProcessedOrderData> transform(ResultSet rs) throws SQLException {
				List<ProcessedOrderData> list = new ArrayList<>();
				while (rs.next()) {
					list.add(clone().complete(rs.getInt("order_id"), rs.getInt("customer_id"), rs.getString("description"), rs.getInt("price"), rs.getString("customer_name")));
				}
				return list;
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<ProcessedOrderData> table) {
				return SQLBuilder.safeSelect(table, new String[] {});
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {

			}

			@Override
			public ProcessedOrderData clone() {
				return new ProcessedOrderData(delivered);
			}

		};
	}

	protected ProcessedOrderData complete(int orderId, int customerId, String description, int price, String customerName) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.description = description;
		this.price = price;
		this.customerName = customerName;

		return this;
	}

	@Override
	public String toString() {
		return "ProcessedOrderData [orderId=" + orderId + ", customerId=" + customerId + ", description=" + description + ", price=" + price + ", customerName=" + customerName + ", delivered=" + delivered + "]";
	}

	@Override
	public ProcessedOrderData clone() {
		return new ProcessedOrderData(delivered);
	}

}
