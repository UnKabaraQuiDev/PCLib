package db;

import java.sql.Timestamp;
import java.util.function.Function;

import lu.pcy113.pclib.db.annotations.entry.Insert;
import lu.pcy113.pclib.db.annotations.entry.Load;
import lu.pcy113.pclib.db.annotations.entry.Update;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.DefaultValue;
import lu.pcy113.pclib.db.autobuild.column.ForeignKey;
import lu.pcy113.pclib.db.autobuild.column.Nullable;
import lu.pcy113.pclib.db.autobuild.column.OnUpdate;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.autobuild.table.Factory;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.SinglePreparedQuery;

public class OrderData implements DataBaseEntry {

	@Column(name = "id")
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column(name = "create_time")
	@DefaultValue("CURRENT_TIMESTAMP")
	// @OnUpdate("CURRENT_TIMESTAMP")
	private Timestamp instant;

	@Column
	@Nullable
	@OnUpdate("CURRENT_TIMESTAMP")
	private Timestamp lastAccess;

	@Column
	@Nullable
	@ForeignKey(table = CustomerTable.class)
	private Long customerId;

	private CustomerData customerData;

	@Query(columns = "customer_id")
	public static Function<Long, SinglePreparedQuery<OrderData>> BY_CUSTOMER_ID;

	private OrderData() {
	}

	public OrderData(long id) {
		this.id = id;
	}

	public OrderData(long id, Timestamp ts) {
		this.id = id;
		this.instant = ts;
	}

	public OrderData(CustomerData customer) {
		this.customerData = customer;
		this.customerId = customer.getId();
	}

	@Insert
	public void insert() {
	}

	@Update
	public void update() {
	}

	@Load
	public void load() {
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@Override
	public String toString() {
		return "OrderData [id=" + id + ", instant=" + instant + ", lastAccess=" + lastAccess + ", customerId=" + customerId
				+ ", customerData=" + customerData + "]";
	}

	@Factory
	public static OrderData empty() {
		return new OrderData(-1);
	}

}
