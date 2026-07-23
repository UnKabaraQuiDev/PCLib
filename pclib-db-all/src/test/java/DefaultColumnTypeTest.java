import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;

@TestInstance(Lifecycle.PER_CLASS)
public class DefaultColumnTypeTest {

	private PreparedStatement dummyStatement;

	@BeforeAll
	public void beforeAll() {
		Map<Integer, Object> values = new HashMap<>();

		this.dummyStatement = (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
				new Class<?>[] { PreparedStatement.class },
				(proxy, method, args) -> {
					if (method.getName().startsWith("set")) {
						values.put((Integer) args[0], args[1]);
						return null;
					}
					throw new UnsupportedOperationException(method.getName());
				});
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
					MySQLDbmsProvider.DBMS_QUALIFIER_NAME,
					SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
					PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME }
	)
	public void test(String dbmsQualifier) throws SQLException {
		final DatabaseEntryUtils dbEntryUtils = new BaseDatabaseEntryUtils(dbmsQualifier);
//		final SQLColumnTypeProvider columnTypeProvider = dbEntryUtils.getColumnTypeProvider();

		this.assertContains(dbEntryUtils, byte.class, () -> (byte) 1);
		this.assertContains(dbEntryUtils, Byte.class, () -> (byte) 1);
		this.assertContains(dbEntryUtils, short.class, () -> (short) 1);
		this.assertContains(dbEntryUtils, Short.class, () -> (short) 1);
		this.assertContains(dbEntryUtils, char.class, () -> (char) 1);
		this.assertContains(dbEntryUtils, Character.class, () -> (char) 1);
		this.assertContains(dbEntryUtils, int.class, () -> (int) 1);
		this.assertContains(dbEntryUtils, Integer.class, () -> (int) 1);
		this.assertContains(dbEntryUtils, long.class, () -> (long) 1);
		this.assertContains(dbEntryUtils, Long.class, () -> (long) 1);
		this.assertContains(dbEntryUtils, float.class, () -> (float) 1);
		this.assertContains(dbEntryUtils, Float.class, () -> (float) 1);
		this.assertContains(dbEntryUtils, double.class, () -> (double) 1);
		this.assertContains(dbEntryUtils, Double.class, () -> (double) 1);
		this.assertContains(dbEntryUtils, BigInteger.class, () -> BigInteger.ONE);
		this.assertContains(dbEntryUtils, BigDecimal.class, () -> BigDecimal.ONE);

		this.assertContains(dbEntryUtils, boolean.class, () -> false);
		this.assertContains(dbEntryUtils, Boolean.class, () -> false);

		this.assertContains(dbEntryUtils, Instant.class, () -> Instant.now());
		this.assertContains(dbEntryUtils, Timestamp.class, () -> Timestamp.from(Instant.now()));
		this.assertContains(dbEntryUtils, LocalDate.class, () -> LocalDate.now());
		this.assertContains(dbEntryUtils, LocalTime.class, () -> LocalTime.now());
		this.assertContains(dbEntryUtils, LocalDateTime.class, () -> LocalDateTime.now());
		this.assertContains(dbEntryUtils, java.sql.Date.class, () -> java.sql.Date.valueOf(LocalDate.now()));
		this.assertContains(dbEntryUtils, java.sql.Time.class, () -> java.sql.Time.valueOf(LocalTime.now()));
		this.assertContains(dbEntryUtils, java.util.Date.class, () -> java.util.Date.from(Instant.now()));
		this.assertContains(dbEntryUtils, OffsetTime.class, () -> OffsetTime.now());
		this.assertContains(dbEntryUtils, OffsetDateTime.class, () -> OffsetDateTime.now());
		this.assertContains(dbEntryUtils, ZonedDateTime.class, () -> ZonedDateTime.now());
		this.assertContains(dbEntryUtils, Period.class, () -> Period.between(LocalDate.now().minusDays(2), LocalDate.now()));
		this.assertContains(dbEntryUtils, Duration.class, () -> Duration.between(LocalDateTime.now().minusDays(2), LocalDateTime.now()));
		this.assertContains(dbEntryUtils, Year.class, () -> Year.now());
		this.assertContains(dbEntryUtils, YearMonth.class, () -> YearMonth.now());

		this.assertContains(dbEntryUtils, byte[].class, () -> new byte[] { 1, 2, 3 });
		this.assertContains(dbEntryUtils, ByteBuffer.class, () -> ByteBuffer.wrap(new byte[] { 1, 2, 3 }));

		this.assertContains(dbEntryUtils, SizeClass.class, () -> SizeClass.NORMAL); // enum
		this.assertContains(dbEntryUtils, JSONObject.class, () -> new JSONObject());
		this.assertContains(dbEntryUtils, JSONArray.class, () -> new JSONArray());

		this.assertContains(dbEntryUtils, String.class, () -> "abc :3");
		this.assertContains(dbEntryUtils, char[].class, () -> "abc :3".toCharArray());
	}

	private <T> void assertContains(DatabaseEntryUtils dbEntryUtils, Class<T> class1, Supplier<T> supplier) throws SQLException {
		final SQLColumnTypeProvider columnTypeProvider = dbEntryUtils.getColumnTypeProvider();
		final ColumnType<T, ?> type = (ColumnType<T, ?>) columnTypeProvider.getTypeFor(class1);
		System.err.println(PCUtils.rightPadString(dbEntryUtils.getDbmsQualifierName(), " ", "postgresql".length()) + " | " + class1 + " -> "
				+ type.getClass());
		type.store(this.dummyStatement, 0, supplier.get());
		type.store(this.dummyStatement, 0, null);
	}

}
