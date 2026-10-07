package test;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.exception.DataAccessException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.utils.DatabaseBuilder;
import lu.kbra.pclib.db.utils.DatabaseBuilder.TablePlan.ColumnPlan;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.StorageBinding;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shared.PersonData;
import shared.PersonTable;

public interface DBBuilderTest extends GenericDBTest {

	@ToString
	@NoArgsConstructor
	public static class MapContainer implements DatabaseEntry {

		private final Map<String, Object> map = new HashMap<>();

		public MapContainer(final Object... vals) {
			PCUtils.toMap(() -> this.map, vals);
		}

	}

	@Data
	public static class MapStorageBinding implements StorageBinding {

		private final String memberName;
		private final Type genericType;

		public MapStorageBinding(final ColumnPlan cp) {
			this.memberName = cp.getName();
			this.genericType = cp.getType().getJavaType();
		}

		@Override
		public void set(final DatabaseEntry entry, final Object val) throws DataAccessException {
			((MapContainer) entry).map.put(this.memberName, val);
		}

		@Override
		public String getMemberName() {
			return this.memberName;
		}

		@Override
		public Type getGenericType() {
			return this.genericType;
		}

		@Override
		public Object get(final DatabaseEntry entry) throws DataAccessException {
			return ((MapContainer) entry).map.get(this.memberName);
		}

	}

	@Test
	default void testSingleTable() {
		final Database db = this.getDatabase();
		final DatabaseBuilder builder = new DatabaseBuilder(db);
		final DatabaseEntryUtils dbEntryUtils = db.getDatabaseEntryUtils();
		db.clearBeans();
		db.drop();
		assert !db.exists();

		//@formatter:off
		builder
				.newTable()
					.name("mainTable")
//					.explicitName("main_table")
					.entryClass(MapContainer.class)
					.beanProvider(DatabaseTable::new)
					.newColumn()
						.name("id")
						.primaryKey(true)
						.autoIncrement(true)
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
						.storagebinding(MapStorageBinding::new)
						.build()
					.newColumn()
						.name("name")
						.explicitName("name_for_some_reason")
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(String.class))
						.storagebinding(MapStorageBinding::new)
						.build()
				.build()
		.build()
		//@formatter:on
				.scanFromBeans()
				.createBeans((t, b) -> {
					if (t instanceof SQLQueryable<?>) {
						assert ((SQLQueryable<?>) t).exists() : "Table doesn't exist: " + ((SQLQueryable<?>) t).getName();
						System.out.println((b ? "Created table: " : "Table existed: ") + ((SQLQueryable<?>) t).getName());
					}
				});

		final AbstractDBTable<MapContainer> table = (AbstractDBTable<MapContainer>) db.getTables().get(0);
		table.insert(new MapContainer("id", null, "name", "name miau"));
		assert "name miau".equals(table.load(new MapContainer("id", 1L)).map.get("name"));
	}

	@Test
	default void testMultiTable() {
		final Database db = this.getDatabase();
		final DatabaseBuilder builder = new DatabaseBuilder(db);
		final DatabaseEntryUtils dbEntryUtils = db.getDatabaseEntryUtils();
		db.clearBeans();
		db.drop();
		assert !db.exists();

		//@formatter:off
		final ColumnPlan col = builder
				.newTable()
					.name("mainTable")
//					.explicitName("main_table")
					.entryClass(MapContainer.class)
					.beanProvider(DatabaseTable::new)
					.newColumn()
						.name("id")
						.primaryKey(true)
						.autoIncrement(true)
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
						.storagebinding(MapStorageBinding::new)
						.build()
					.newColumn()
						.name("name")
						.explicitName("name_for_some_reason")
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(String.class))
						.storagebinding(MapStorageBinding::new)
						.build()
				.build()
				.newTable()
					.name("secondTablexD")
	//				.explicitName("main_table")
					.entryClass(MapContainer.class)
					.beanProvider(DatabaseTable::new)
					.newColumn()
						.name("second_id")
						.primaryKey(true);
		if (!dbEntryUtils.getDbmsQualifierName().equals(SQLiteDbmsProvider.DBMS_QUALIFIER_NAME))
		col				.autoIncrement(true);
		col
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
						.storagebinding(MapStorageBinding::new)
						.build()
					.newColumn()
						.name("linkToMainTable")
						.primaryKey(true)
						.foreignKeyTable((Class<? extends SQLQueryable<?>>) (Class) DatabaseTable.class)
						.foreignKeyTableName("mainTable")
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
						.storagebinding(MapStorageBinding::new)
						.build()
				.build()
		.build()
		//@formatter:on
				.scanFromBeans()
				.createBeans((t, b) -> {
					if (t instanceof SQLQueryable<?>) {
						assert ((SQLQueryable<?>) t).exists() : "Table doesn't exist: " + ((SQLQueryable<?>) t).getName();
						System.out.println((b ? "Created table: " : "Table existed: ") + ((SQLQueryable<?>) t).getName());
					}
				});

		final AbstractDBTable<MapContainer> mainTable = (AbstractDBTable<MapContainer>) db.getTables().get(0);
		final MapContainer value = mainTable.insert(new MapContainer("id", null, "name", "name miau"));
		value.map.remove("name");
		assert "name miau".equals(mainTable.load(value).map.get("name"));
		assert mainTable.load(value).map.get("id") != null;

		final AbstractDBTable<MapContainer> secondTable = (AbstractDBTable<MapContainer>) db.getTables().get(1);
		final MapContainer value2 = secondTable.insert(new MapContainer("second_id", 2L, "linkToMainTable", value.map.get("id")));
		assert !secondTable.load(value2).map.containsKey("name");
	}

	@Test
	default void testHybridTables() {
		final Database db = this.getDatabase();
		final DatabaseBuilder builder = new DatabaseBuilder(db);
		final DatabaseEntryUtils dbEntryUtils = db.getDatabaseEntryUtils();
		db.clearBeans();
		db.drop();
		assert !db.exists();

		final PersonTable personTable = new PersonTable(db);
		db.registerTable(personTable);

		//@formatter:off
		builder
				.newTable()
					.name("mainTable")
//					.explicitName("main_table")
					.entryClass(MapContainer.class)
					.beanProvider(DatabaseTable::new)
					.newColumn()
						.name("id")
						.primaryKey(true)
						.autoIncrement(true)
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
						.storagebinding(MapStorageBinding::new)
						.build()
					.newColumn()
						.name("name")
						.explicitName("name_for_some_reason")
						.uniqueGroupId(0)
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(String.class))
						.storagebinding(MapStorageBinding::new)
						.build()
					.newColumn()
						.name("personId")
						.foreignKeyTable(PersonTable.class)
						.nullable(true)
						.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Integer.class))
						.storagebinding(MapStorageBinding::new)
						.build()
				.build()
		.build()
		//@formatter:on
				.scanFromBeans()
				.createBeans((t, b) -> {
					if (t instanceof SQLQueryable<?>) {
						assert ((SQLQueryable<?>) t).exists() : "Table doesn't exist: " + ((SQLQueryable<?>) t).getName();
						System.out.println((b ? "Created table: " : "Table existed: ") + ((SQLQueryable<?>) t).getName());
					}
				});

		final AbstractDBTable<MapContainer> table = (AbstractDBTable<MapContainer>) db.getTables()
				.stream()
				.filter(c -> "main".equals(c.getName()))
				.findFirst()
				.get();
		table.insertAndReload(new MapContainer("id", null, "name", "name miau"));
		final MapContainer value = table.load(new MapContainer("id", 1L));
		assert "name miau".equals(value.map.get("name"));
		final PersonData person = personTable.insertAndReload(new PersonData("person's name", new Date(System.currentTimeMillis())));
		value.map.put("personId", person.getId());
		assert table.updateAndReload(value).map.get("personId") == (Integer) person.getId();

	}

}
