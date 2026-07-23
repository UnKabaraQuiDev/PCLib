import java.sql.SQLException;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;

public class DefaultEncodingTypeTest {

	@ParameterizedTest
	@ValueSource(
			strings = {
					MySQLDbmsProvider.DBMS_QUALIFIER_NAME,
					SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
					PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME }
	)
	public void test(String dbmsQualifier) throws SQLException {
		final DatabaseEntryUtils dbEntryUtils = new BaseDatabaseEntryUtils(dbmsQualifier);

		this.assertSingle(dbEntryUtils, byte.class);
		this.assertSingle(dbEntryUtils, Byte.class);
		this.assertSingle(dbEntryUtils, short.class);
		this.assertSingle(dbEntryUtils, Short.class);
//		this.assertSingle(dbEntryUtils, char.class);
//		this.assertSingle(dbEntryUtils, Character.class);
		this.assertSingle(dbEntryUtils, int.class);
		this.assertSingle(dbEntryUtils, Integer.class);
		this.assertSingle(dbEntryUtils, long.class);
		this.assertSingle(dbEntryUtils, Long.class);
		this.assertSingle(dbEntryUtils, float.class);
		this.assertSingle(dbEntryUtils, Float.class);
		this.assertSingle(dbEntryUtils, double.class);
		this.assertSingle(dbEntryUtils, Double.class);
//		this.assertSingle(dbEntryUtils, BigInteger.class);
//		this.assertSingle(dbEntryUtils, BigDecimal.class);

//		this.assertSingle(dbEntryUtils, boolean.class);
//		this.assertSingle(dbEntryUtils, Boolean.class);

//		this.assertSingle(dbEntryUtils, java.sql.Date.class);
//		this.assertSingle(dbEntryUtils, Timestamp.class);
//		this.assertSingle(dbEntryUtils, Time.class);

		this.assertSingle(dbEntryUtils, String.class);

		this.assertSingle(dbEntryUtils, byte[].class);
	}

	public <T> void assertSingle(DatabaseEntryUtils dbEntryUtils, Class<T> class1) {
		final SQLEncodingTypeProvider encodingTypeProvider = dbEntryUtils.getEncodingTypeProvider();
		if (encodingTypeProvider.computeType(class1, HintsOwner.EMPTY).count() > 1) {
			System.err.println(
					PCUtils.rightPadString(dbEntryUtils.getDbmsQualifierName(), " ", "postgresql".length()) + " | " + class1 + " -> "
							+ encodingTypeProvider.computeType(class1, HintsOwner.EMPTY)
									.map(c -> "\t\t" + c.getCreatedType() + " (" + c.getStoredType() + ")")
									.collect(Collectors.joining("\n *", "\n *", "")));
//			Assertions.fail(class1 + " matching:\n"
//					+ encodingTypeProvider.computeType(class1, HintsOwner.EMPTY).map(c -> c.toString()).collect(Collectors.joining("\n")));
		}
		final EncodingType<T> type = encodingTypeProvider.getTypeFor(class1);
		System.err.println(PCUtils.rightPadString(dbEntryUtils.getDbmsQualifierName(), " ", "postgresql".length()) + " | " + class1 + " -> "
				+ type.getClass());
	}

}
