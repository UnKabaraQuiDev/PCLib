import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class BaseProxyDataBaseEntryUtilsTests {

	private final BaseProxyDataBaseEntryUtils utils = new BaseProxyDataBaseEntryUtils();

	@Test
	public void buildMethodQueryFunctionDetectsAutoStrategyFromReturnTypeAndAnnotations() throws Exception {
		assertDetectedType("defaultEntry", Query.Type.FIRST_NULL);
		assertDetectedType("nullableEntry", Query.Type.FIRST_NULL);
		assertDetectedType("optionalEntry", Query.Type.FIRST_NULL);
		assertDetectedType("listEntry", Query.Type.LIST_EMPTY);
		assertDetectedType("nonNullEntry", Query.Type.FIRST_THROW);
		assertDetectedType("notNullEntry", Query.Type.FIRST_THROW);
	}

	@Test
	public void buildMethodQueryFunctionKeepsExplicitStrategy() throws Exception {
		assertDetectedType("explicitSingleThrow", Query.Type.SINGLE_THROW);
		assertDetectedType("explicitSingleNull", Query.Type.SINGLE_NULL);
		assertDetectedType("explicitListNullOverridesListDefault", Query.Type.LIST_NULL);
		assertDetectedType("explicitListThrowOverridesListDefault", Query.Type.LIST_THROW);
	}

	@Test
	public void buildMethodQueryFunctionReplacesTableNamePlaceholder() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("defaultEntry");

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction("people", table, method);
		function.apply(Collections.emptyList());

		assertNotNull(table.lastQuery);
		assertEquals("SELECT * FROM `people`", table.lastQuery.getPreparedQuerySQL(table));
	}

	private void assertDetectedType(final String methodName, final Query.Type expectedType) throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod(methodName);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction("people", table, method);
		function.apply(Collections.emptyList());

		assertNotNull(table.lastQuery);
		assertEquals(expectedType, extractQueryType(table.lastQuery), methodName);
	}

	private static Query.Type extractQueryType(final SQLQuery<DummyEntry, ?> query) throws Exception {
		final Field typeField = query.getClass().getDeclaredField("type");
		typeField.setAccessible(true);
		return (Query.Type) typeField.get(query);
	}

	private interface QueryMethods {

		@Query("SELECT * FROM {NAME}")
		DummyEntry defaultEntry();

		@Nullable
		@Query("SELECT * FROM {NAME}")
		DummyEntry nullableEntry();

		@NonNull
		@Query("SELECT * FROM {NAME}")
		DummyEntry nonNullEntry();

		@NotNull
		@Query("SELECT * FROM {NAME}")
		DummyEntry notNullEntry();

		@Query("SELECT * FROM {NAME}")
		Optional<DummyEntry> optionalEntry();

		@Query("SELECT * FROM {NAME}")
		List<DummyEntry> listEntry();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.SINGLE_THROW)
		DummyEntry explicitSingleThrow();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.SINGLE_NULL)
		DummyEntry explicitSingleNull();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.LIST_NULL)
		List<DummyEntry> explicitListNullOverridesListDefault();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.LIST_THROW)
		List<DummyEntry> explicitListThrowOverridesListDefault();

	}

	private static final class DummyEntry implements DataBaseEntry {
	}

	private static final class CaptureQueryable implements SQLQueryable<DummyEntry> {

		private SQLQuery<DummyEntry, ?> lastQuery;

		@Override
		public int count() throws DBException {
			return 0;
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) throws DBException {
			this.lastQuery = query;
			return null;
		}

		@Override
		public Class<? extends SQLQueryable<DummyEntry>> getTargetClass() {
			return CaptureQueryable.class;
		}

		@Override
		public DataBaseEntryUtils getDbEntryUtils() {
			return new BaseProxyDataBaseEntryUtils();
		}

		@Override
		public String getName() {
			return "people";
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface Nullable {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface NonNull {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface NotNull {}

}
