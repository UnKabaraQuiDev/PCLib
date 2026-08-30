package test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import shared.PersonData;
import shared.PersonTable;

public interface DBTransactionTest extends GenericDBTest {

	@Test
	default void testTransaction() throws SQLException {
		final PersonTable people = new PersonTable(this.getDatabase());
		people.getQueryableHookManager().add(new VersionRule());
		new DatabaseScanner(this.getDatabase(), null).register(people).doScan();
		System.err.println(Arrays.toString(people.getCreateSQL()));
		people.create();
		people.truncate();

		final Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);

		try (DBTransaction tt = this.getDatabase().createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);

			tt.rollback();
		}
		assert !people.exists(p1);

		try (DBTransaction tt = this.getDatabase().createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);
		}
		assert !people.exists(p1);

		try (DBTransaction tt = this.getDatabase().createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);

			tt.commit();
		}
		assert people.exists(p1);

		people.delete(p1);
	}

}
