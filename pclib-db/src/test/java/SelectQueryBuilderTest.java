import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.Join;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class SelectQueryBuilderTest {

	private static final class DummyEntry implements DataBaseEntry {
	}

	private static final class DummyQueryable implements SQLQueryable<DummyEntry> {
		private final String name;

		private DummyQueryable(final String name) {
			this.name = name;
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<? extends SQLQueryable<DummyEntry>> getTargetClass() {
			return (Class<? extends SQLQueryable<DummyEntry>>) (Class<?>) DummyQueryable.class;
		}

		@Override
		public DataBaseEntryUtils getDbEntryUtils() {
			return null;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Test
	public void selectBuilderBuildsNestedWhereJoinOrderAndPagingSql() {
		final DummyQueryable joinTable = new DummyQueryable("audit_log");

		final String sql = QueryBuilder.<DummyEntry>select()
				.select("`person`.`id`")
				.select("COUNT(*) AS `match_count`")
				.join(Join.Type.LEFT, joinTable, "log", "`log`.`person_id` = `person`.`id`")
				.where(cb -> cb.match("`person`.`active`", "=", true)
						.and(nested -> nested.match("`person`.`role`", "=", "admin")
								.or(or -> or.in("`person`.`id`", java.util.Arrays.asList(1, 2, 3)))))
				.orderByAsc("person.id")
				.orderByFunc("RAND()")
				.limit(25)
				.offset(10)
				.toString();

		Assertions.assertEquals("SELECT `person`.`id`, COUNT(*) AS `match_count` FROM `[NAME]` "
				+ "LEFT JOIN `audit_log` AS `log` ON `log`.`person_id` = `person`.`id` "
				+ "WHERE (`person`.`active` = ? AND (`person`.`role` = ? OR `person`.`id` IN  (?, ?, ?))) "
				+ "ORDER BY `person`.`id` ASC, RAND() LIMIT 25 OFFSET 10;", sql);
	}

	@Test
	public void offsetRejectsNegativeValues() {
		final SelectQueryBuilder<DummyEntry> builder = QueryBuilder.select();
		final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> builder.offset(-1));
		Assertions.assertEquals("Offset cannot be negative.", ex.getMessage());
	}

	@Test
	public void listRejectsExplicitSelectColumns() {
		final SelectQueryBuilder<DummyEntry> builder = QueryBuilder.<DummyEntry>select().select("`id`");
		final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, builder::list);
		Assertions.assertEquals("You specified the following explicit rows: [`id`]", ex.getMessage());
	}

	@Test
	public void countBuildsAggregateQuery() {
		final RawTransformingQuery<DummyEntry, Integer> query = QueryBuilder.<DummyEntry>select()
				.where(cb -> cb.match("`active`", "=", true))
				.count();

		Assertions.assertEquals("SELECT COUNT(*) AS `count` FROM `people` WHERE `active` = ? LIMIT 500;",
				query.getPreparedQuerySQL(new DummyQueryable("people")));
	}

}
