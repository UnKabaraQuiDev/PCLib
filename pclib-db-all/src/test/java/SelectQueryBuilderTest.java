import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.Join;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

public class SelectQueryBuilderTest {

	@Data
	@NoArgsConstructor
	private static final class DummyEntry implements DataBaseEntry {
		@Override
		public SelectQueryBuilderTest.DummyEntry clone() {
			return PCUtils.safeClone(super::clone);
		}
	}

	private static final class DummyQueryable implements SQLQueryable<DummyEntry> {

		private final DataBaseEntryUtils dataBaseEntryUtils;

		public DummyQueryable(final DataBaseEntryUtils dataBaseEntryUtils) {
			this.dataBaseEntryUtils = dataBaseEntryUtils;
		}

		private DummyQueryable(final String protocol) {
			this.dataBaseEntryUtils = new BaseDataBaseEntryUtils(protocol);
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public DataBaseEntryUtils getDataBaseEntryUtils() {
			return this.dataBaseEntryUtils;
		}

		@Override
		public Class<? extends SQLQueryable<DummyEntry>> getTargetClass() {
			return (Class<? extends SQLQueryable<DummyEntry>>) (Class<?>) DummyQueryable.class;
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<? extends DummyEntry> getEntryClass() {
			return DummyEntry.class;
		}

		@Override
		public DataBase getDatabase() {
			throw new UnsupportedOperationException();
		}

	}

	@Test
	public void buildersRenderPostgreSQLIdentifierQuotingThroughVisitor() {
		final String sql = QueryBuilder.<DummyEntry>select()
				.where(cb -> cb.match("\"dummy_queryable\".\"active\"", "=", true))
				.orderByAsc("\"dummy_queryable\".\"id\"")
				.build(new PostgreSQLStructureVisitor(), new DummyQueryable(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME));

		Assertions.assertEquals(
				"SELECT * FROM \"public\".\"dummy_queryable\" WHERE \"dummy_queryable\".\"active\" = ? ORDER BY \"dummy_queryable\".\"id\" ASC LIMIT "
						+ QueryBuilder.DEFAULT_ENTRY_LIMIT + ";",
				sql);
	}

	@Test
	public void countBuildsAggregateQuery() {
		final RawTransformingQuery<DummyEntry, Integer> query = QueryBuilder.<DummyEntry>select()
				.where(cb -> cb.match("`active`", "=", true))
				.count();

		Assertions.assertEquals(
				"SELECT COUNT(*) AS `count` FROM `dummy_queryable` WHERE `active` = ? LIMIT " + QueryBuilder.DEFAULT_ENTRY_LIMIT + ";",
				query.getPreparedQuerySQL(new DummyQueryable(MySQLDbmsProvider.DBMS_QUALIFIER_NAME)));
	}

	@Test
	public void listRejectsExplicitSelectColumns() {
		final SelectQueryBuilder<DummyEntry> builder = QueryBuilder.<DummyEntry>select().select("`id`");
		final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, builder::list);
		Assertions.assertEquals("You specified the following explicit rows: [`id`]", ex.getMessage());
	}

	@Test
	public void offsetRejectsNegativeValues() {
		final SelectQueryBuilder<DummyEntry> builder = QueryBuilder.select();
		final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> builder.offset(-1));
		Assertions.assertEquals("Offset cannot be negative.", ex.getMessage());
	}

	@Test
	public void selectBuilderBuildsNestedWhereJoinOrderAndPagingSql() {
		final DummyQueryable dummy = new DummyQueryable(MySQLDbmsProvider.DBMS_QUALIFIER_NAME);
		final DummyQueryable joinTable = new DummyQueryable(MySQLDbmsProvider.DBMS_QUALIFIER_NAME);

		final String sql = QueryBuilder.<DummyEntry>select()
				.select("`dummy_queryable`.`id`")
				.select("COUNT(*) AS `match_count`")
				.join(Join.Type.LEFT, joinTable, "log", "`log`.`dummy_queryable_id` = `dummy_queryable`.`id`")
				.where(cb -> cb.match("`dummy_queryable`.`active`", "=", true)
						.and(nested -> nested.match("`dummy_queryable`.`role`", "=", "admin")
								.or(or -> or.in("`dummy_queryable`.`id`", java.util.Arrays.asList(1, 2, 3)))))
				.orderByAsc("`dummy_queryable`.`id`")
				.orderByFunc("RAND()")
				.limit(25)
				.offset(10)
				.build(dummy.getDataBaseEntryUtils().getStructureVisitor(), dummy);

		Assertions.assertEquals("SELECT `dummy_queryable`.`id`, COUNT(*) AS `match_count` FROM `dummy_queryable` "
				+ "LEFT JOIN `dummy_queryable` AS `log` ON `log`.`dummy_queryable_id` = `dummy_queryable`.`id` "
				+ "WHERE (`dummy_queryable`.`active` = ? AND (`dummy_queryable`.`role` = ? OR `dummy_queryable`.`id` IN  (?, ?, ?))) "
				+ "ORDER BY `dummy_queryable`.`id` ASC, RAND() LIMIT 25 OFFSET 10;", sql);
	}

	@Test
	public void sqlBuilderRendersPostgreSQLIdentifierQuotingThroughVisitor() {
		final String sql = new PostgreSQLStructureVisitor()
				.safeSelect(new DummyQueryable(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME), new String[] { "name" }, true, true);

		Assertions.assertEquals("SELECT * FROM \"public\".\"dummy_queryable\" WHERE \"name\" = ? LIMIT ? OFFSET ?;", sql);
	}

}
