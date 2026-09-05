import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.ColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@TestInstance(Lifecycle.PER_CLASS)
public class EncodingTypeTest {

	private PreparedStatement statement;
	private ResultSet resultSet;

	private final Map<Object, Object> values = new HashMap<>();

	@BeforeEach
	public void beforeEach() {
		this.values.clear();

		this.statement = (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
				new Class<?>[] { PreparedStatement.class },
				(proxy, method, args) -> {

					final String name = method.getName();

					if (name.startsWith("set")) {
						this.values.put(args[0], args.length > 1 ? args[1] : null);
						return null;
					}

					throw new UnsupportedOperationException(name);
				});

		this.resultSet = (ResultSet) Proxy
				.newProxyInstance(ResultSet.class.getClassLoader(), new Class<?>[] { ResultSet.class }, (proxy, method, args) -> {

					final String name = method.getName();

					if (name.startsWith("get")) {
						return this.values.get(args[0]);
					}

					if ("wasNull".equals(name)) {
						return false;
					}

					throw new UnsupportedOperationException(name);
				});
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
					MySQLDbmsProvider.DBMS_QUALIFIER_NAME,
					SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
					PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME }
	)
	public void test(final String dbmsQualifier) throws SQLException {
		final DatabaseEntryUtils dbEntryUtils = new BaseDatabaseEntryUtils(dbmsQualifier);

		final ColumnTypeProvider provider = dbEntryUtils.getColumnTypeProvider();

		this.assertRoundTrip(provider, byte.class, (byte) 0, (byte) 1, (byte) -1);
		this.assertRoundTrip(provider, Byte.class, (byte) 0, (byte) 1, (byte) -1);

		this.assertRoundTrip(provider, short.class, (short) 0, (short) 1, (short) -1);
		this.assertRoundTrip(provider, Short.class, (short) 0, (short) 1, (short) -1);

		this.assertRoundTrip(provider, char.class, 'a', 'z', '0', (char) 0);
		this.assertRoundTrip(provider, Character.class, 'a', 'z', '0');

		this.assertRoundTrip(provider, int.class, 0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);
		this.assertRoundTrip(provider, Integer.class, 0, 1, -1, Integer.MAX_VALUE);

		this.assertRoundTrip(provider, long.class, 0L, 1L, -1L, Long.MAX_VALUE, Long.MIN_VALUE);
		this.assertRoundTrip(provider, Long.class, 0L, 1L, -1L, Long.MAX_VALUE);

		this.assertRoundTrip(provider, float.class, 0.0f, 1.5f, -1.5f);
		this.assertRoundTrip(provider, Float.class, 0.0f, 1.5f, -1.5f);

		this.assertRoundTrip(provider, double.class, 0.0d, 1.5d, -1.5d);
		this.assertRoundTrip(provider, Double.class, 0.0d, 1.5d, -1.5d);

		this.assertRoundTrip(provider, BigInteger.class, BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(-123456789));
		this.assertRoundTrip(provider,
				BigDecimal.class,
				BigDecimal.ZERO,
				BigDecimal.ONE,
				new BigDecimal("123.456"),
				new BigDecimal("-123.456"));

		this.assertRoundTrip(provider, boolean.class, true, false);
		this.assertRoundTrip(provider, Boolean.class, true, false);

		this.assertRoundTrip(provider, Instant.class, Instant.EPOCH, Instant.now(), Instant.now().minusSeconds(10000));

		this.assertRoundTrip(provider, Timestamp.class, Timestamp.from(Instant.EPOCH), Timestamp.from(Instant.now()));

		this.assertRoundTrip(provider, LocalDate.class, LocalDate.of(1970, 1, 1), LocalDate.now());

		this.assertRoundTrip(provider, LocalTime.class, LocalTime.MIDNIGHT, LocalTime.NOON, LocalTime.of(12, 34, 56));

		this.assertRoundTrip(provider, LocalDateTime.class, LocalDateTime.of(1970, 1, 1, 0, 0), LocalDateTime.now());

		this.assertRoundTrip(provider, java.sql.Date.class, java.sql.Date.valueOf("1970-01-01"), java.sql.Date.valueOf(LocalDate.now()));

		this.assertRoundTrip(provider, java.sql.Time.class, java.sql.Time.valueOf("00:00:00"), java.sql.Time.valueOf(LocalTime.now()));

		this.assertRoundTrip(provider, java.util.Date.class, java.util.Date.from(Instant.EPOCH), java.util.Date.from(Instant.now()));

		this.assertRoundTrip(provider, OffsetTime.class, OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC), OffsetTime.now());

		this.assertRoundTrip(provider, OffsetDateTime.class, OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC), OffsetDateTime.now());

		this.assertRoundTrip(provider, ZonedDateTime.class, ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC), ZonedDateTime.now());

		this.assertRoundTrip(provider, Period.class, Period.ZERO, Period.ofDays(1), Period.ofMonths(2));

		this.assertRoundTrip(provider, Duration.class, Duration.ZERO, Duration.ofSeconds(1), Duration.ofDays(2));

		this.assertRoundTrip(provider, Year.class, Year.of(1970), Year.now());

		this.assertRoundTrip(provider, YearMonth.class, YearMonth.of(1970, 1), YearMonth.now());

		this.assertRoundTrip(provider, byte[].class, new byte[0], new byte[] { 1 }, new byte[] { 1, 2, 3 });

		this.assertRoundTrip(provider,
				ByteBuffer.class,
				ByteBuffer.allocate(0),
				ByteBuffer.wrap(new byte[] { 1 }),
				ByteBuffer.wrap(new byte[] { 1, 2, 3 }));

		this.assertRoundTrip(provider, SizeClass.class, SizeClass.NORMAL, SizeClass.values()[0]);

		this.assertRoundTrip(provider, JSONObject.class, new JSONObject(), new JSONObject("{\"test\":123}"));

		this.assertRoundTrip(provider, JSONArray.class, new JSONArray(), new JSONArray("[1,2,3]"));

		this.assertRoundTrip(provider, String.class, "", "a", "abc", "hello world", "abc :3");

		this.assertRoundTrip(provider, char[].class, new char[0], new char[] { 'a' }, new char[] { 'a', 'b', 'c' });
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	private final <T> void assertRoundTrip(final ColumnTypeProvider provider, final Class<T> typeClass, final T... inputs)
			throws SQLException {
		this.values.clear();

		final ColumnType<T, ?> columnType = (ColumnType<T, ?>) provider.getTypeFor(typeClass);
		final EncodingType<T> encodingType = (EncodingType<T>) columnType.getEncodingType();

		Assertions.assertNotNull(encodingType, "No EncodingType registered for " + typeClass.getName());

		System.err.println(typeClass.getName() + " -> " + columnType.getClass().getName() + " -> " + encodingType.getClass().getName());

		final Type firstType = inputs[0].getClass();
		for (final T input : inputs) {
			this.values.clear();

			columnType.store(this.statement, 1, input);
			final T result = columnType.load(this.resultSet, 1, firstType);

			if (input instanceof byte[] && result instanceof byte[]) {
				final byte[] expected = (byte[]) input;
				final byte[] actual = (byte[]) result;

				Assertions.assertArrayEquals(expected,
						actual,
						() -> "Failed for " + typeClass.getName() + " with " + Arrays.toString(expected));

			} else if (input instanceof char[] && result instanceof char[]) {
				final char[] expected = (char[]) input;
				final char[] actual = (char[]) result;

				Assertions.assertArrayEquals(expected,
						actual,
						() -> "Failed for " + typeClass.getName() + " with " + Arrays.toString(expected));

			} else if (input instanceof JSONObject && result instanceof JSONObject) {
				Assertions.assertTrue(((JSONObject) input).similar(result), () -> "Failed for " + typeClass.getName() + " with " + input);
			} else if (input instanceof JSONArray && result instanceof JSONArray) {
				Assertions.assertTrue(((JSONArray) input).similar(result), () -> "Failed for " + typeClass.getName() + " with " + input);
			} else {
				Assertions.assertEquals(input, result, () -> "Failed for " + typeClass.getName() + " with " + input);
			}
		}
	}

}
