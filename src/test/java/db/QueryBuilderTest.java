package db;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.db.impl.SQLNamed;
import lu.pcy113.pclib.db.query.QueryBuilder;

public class QueryBuilderTest {

	@Test
	public void select() {
		final String sel1 = QueryBuilder.select().limit(10).offset(5).where(c -> c.match("a", "=", (String) "b")).toString();
		System.out.println(sel1);
		assert sel1.equals("SELECT * FROM `" + SQLNamed.MOCK.getName() + "` WHERE a = ? LIMIT 10 OFFSET 5;") : "Select 1 not matching";

		final String sel2 = QueryBuilder
				.select()
				.limit(10)
				.offset(5)
				.where(c -> c.match("x = 10").and(ccd -> ccd.match("abc").or(cd -> cd.match("b", "=", (String) "c").match("qds"))))
				.toString();
		System.out.println(sel2);
		assert sel2.equals("SELECT * FROM `" + SQLNamed.MOCK.getName() + "` WHERE (x = 10 AND (abc OR (b = ? AND qds))) LIMIT 10 OFFSET 5;")
				: "Select 2 not matching";

		final String sel3 = QueryBuilder
				.select()
				.limit(10)
				.offset(5)
				.where(c -> c.and(cd -> cd.match("a").match("b").or(ccd -> ccd.match("c"))))
				.toString();
		System.out.println(sel3);
		assert sel3.equals("SELECT * FROM `" + SQLNamed.MOCK.getName() + "` WHERE ((a AND b) OR c) LIMIT 10 OFFSET 5;")
				: "Select 3 not matching";

		final String sel4 = QueryBuilder
				.select()
				.limit(10)
				.offset(5)
				.where(c -> c.match("a").and(ccd -> ccd.match("b").or(cd -> cd.match("c"))))
				.toString();
		System.out.println(sel4);
		assert sel4.equals("SELECT * FROM `" + SQLNamed.MOCK.getName() + "` WHERE (a AND (b OR c)) LIMIT 10 OFFSET 5;")
				: "Select 4 not matching";
	}

}
