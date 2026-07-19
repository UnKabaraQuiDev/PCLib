import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;

public class DefaultColumnTypeTest {

	@ParameterizedTest
	@ValueSource(
			strings = {
					MySQLDbmsProvider.DBMS_QUALIFIER_NAME,
					SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
					PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME }
	)
	public void test(String dbmsQualifier) {
		final DatabaseEntryUtils dbEntryUtils = new BaseDatabaseEntryUtils(dbmsQualifier);
//		final SQLColumnTypeProvider columnTypeProvider = dbEntryUtils.getColumnTypeProvider();

		assertContains(dbEntryUtils, Timestamp.class);
		assertContains(dbEntryUtils, LocalDate.class);
		assertContains(dbEntryUtils, LocalDateTime.class);
		assertContains(dbEntryUtils, Date.class);
		assertContains(dbEntryUtils, java.util.Date.class);
	}

	private void assertContains(DatabaseEntryUtils dbEntryUtils, Class<?> class1) {
		final SQLColumnTypeProvider columnTypeProvider = dbEntryUtils.getColumnTypeProvider();
		try {
			final ColumnType type = columnTypeProvider.getTypeFor(class1);
			System.err.println(PCUtils.rightPadString(dbEntryUtils.getDbmsQualifierName(), " ", "postgresql".length()) + " | " + class1
					+ " -> " + type.getClass());
		} catch (Exception e) {
			Assert.fail("No type for: " + class1 + " (" + dbEntryUtils.getDbmsQualifierName() + ")");
		}
	}

}
