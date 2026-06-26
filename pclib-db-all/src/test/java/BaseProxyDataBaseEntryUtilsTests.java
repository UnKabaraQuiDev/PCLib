import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.OrderBy.Type;
import lu.kbra.pclib.db.autobuild.query.Limit;
import lu.kbra.pclib.db.autobuild.query.Offset;
import lu.kbra.pclib.db.autobuild.query.Param;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

public class BaseProxyDataBaseEntryUtilsTests {

	private static final class CaptureQueryable implements SQLQueryable<DummyEntry> {

		private SQLQuery<DummyEntry, ?> lastQuery;

		@Override
		public int count() throws DBException {
			return 0;
		}

		@Override
		public DataBaseEntryUtils getDataBaseEntryUtils() {
			return new BaseProxyDataBaseEntryUtils("mysql");
		}

		@Override
		public String getName() {
			return "people";
		}

		@Override
		public Class<? extends SQLQueryable<DummyEntry>> getTargetClass() {
			return CaptureQueryable.class;
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) throws DBException {
			this.lastQuery = query;
			return null;
		}

		@Override
		public String getQualifiedName() {
			return getName();
		}

	}

	@Data
	@NoArgsConstructor
	private static final class DummyEntry implements DataBaseEntry {
		@Override
		public BaseProxyDataBaseEntryUtilsTests.DummyEntry clone() {
			return PCUtils.safeClone(super::clone);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface NonNull {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface NotNull {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface Nullable {}

	private interface QueryMethods {

		@Query
		List<DummyEntry> allComparators(
				@Param(value = "name", comparator = "LIKE") String likeName,
				@Param(value = "age", comparator = "=") int equalAge,
				@Param(value = "age", comparator = "<") int lowerThanAge,
				@Param(value = "age", comparator = "<=") int lowerOrEqualAge,
				@Param(value = "age", comparator = ">") int greaterThanAge,
				@Param(value = "age", comparator = ">=") int greaterOrEqualAge);

		@Query(columns = { "name" }, limit = 1, offset = 2)
		List<DummyEntry> columnBasedQuery(String name, int limit, int offset);

		@Query("SELECT * FROM {NAME}")
		DummyEntry defaultEntry();

		@Query
		List<DummyEntry> duplicateLimit(@Limit int firstLimit, @Limit int secondLimit);

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.LIST_NULL)
		List<DummyEntry> explicitListNullOverridesListDefault();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.LIST_THROW)
		List<DummyEntry> explicitListThrowOverridesListDefault();

		@Query(value = "SELECT COUNT(*) FROM {NAME}", strategy = Query.Type.SINGLE_THROW)
		int explicitPrimitiveSingleThrow();

		@Query(value = "SELECT `name` FROM {NAME}", strategy = Query.Type.LIST_NULL)
		List<String> explicitScalarListNull();

		@Query(value = "SELECT `name` FROM {NAME}", strategy = Query.Type.SINGLE_THROW)
		String explicitScalarSingleThrow();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.SINGLE_NULL)
		DummyEntry explicitSingleNull();

		@Query(value = "SELECT * FROM {NAME}", strategy = Query.Type.SINGLE_THROW)
		DummyEntry explicitSingleThrow();

		@Query
		List<DummyEntry> invalidComparator(@Param(value = "name", comparator = "!=") String name);

		@Query
		List<DummyEntry> limitedQuery(@Limit int limit, @Offset int offset);

		@Query
		List<String> limitedScalarQuery(@Limit int limit, @Offset int offset);

		@Query("SELECT * FROM {NAME}")
		List<DummyEntry> listEntry();

		@Query
		List<String> listScalarParameterQuery(@Param(value = "age", comparator = ">=") Integer minAge);

		@Query("SELECT `name` FROM {NAME}")
		List<String> listScalarString();

		@Query("SELECT COUNT(*) FROM {NAME} WHERE `name` = ?")
		int manualPrimitiveCountByName(String name);

		@Query("SELECT `name` FROM {NAME} WHERE `age` >= ?")
		String manualScalarStringByAge(int minAge);

		@Query
		List<DummyEntry> multipleParameterAnnotations(@Param("age") @Limit int age);

		@NonNull
		@Query("SELECT * FROM {NAME}")
		DummyEntry nonNullEntry();

		@NonNull
		@Query("SELECT `name` FROM {NAME}")
		String nonNullScalarString();

		@NotNull
		@Query("SELECT * FROM {NAME}")
		DummyEntry notNullEntry();

		@NotNull
		@Query("SELECT `name` FROM {NAME}")
		String notNullScalarString();

		@Nullable
		@Query("SELECT * FROM {NAME}")
		DummyEntry nullableEntry();

		@Query("SELECT * FROM {NAME}")
		Optional<DummyEntry> optionalEntry();

		@Query
		Optional<String> optionalScalarParameterQuery(@Param("name") String name);

		@Query("SELECT `name` FROM {NAME}")
		Optional<String> optionalScalarString();

		@Query
		List<DummyEntry> parameterQuery(
				@Param(value = "name", comparator = "LIKE", ignoreNull = true) String name,
				@Param(value = "age", comparator = ">=", ignoreNull = true) Integer minAge,
				@Limit int limit,
				@Offset int offset);

		@Query
		List<DummyEntry> parameterQueryKeepingNull(@Param("name") String name);

		@Query(orderBy = { @OrderBy(column = "name", type = Type.ASC) })
		List<DummyEntry> parameterQueryWithOrderBy(@Param("name") String name);

		@Query("SELECT COUNT(*) FROM {NAME}")
		int primitiveInt();

		@Query
		int primitiveParameterQuery(@Param("name") String name);

		@Query
		List<String> scalarAllComparators(
				@Param(value = "name", comparator = "LIKE") String likeName,
				@Param(value = "age", comparator = "=") int equalAge,
				@Param(value = "age", comparator = "<") int lowerThanAge,
				@Param(value = "age", comparator = "<=") int lowerOrEqualAge,
				@Param(value = "age", comparator = ">") int greaterThanAge,
				@Param(value = "age", comparator = ">=") int greaterOrEqualAge);

		@Query(columns = { "name" }, limit = 1, offset = 2)
		List<String> scalarColumnBasedQuery(String name, int limit, int offset);

		@Query
		List<String> scalarDuplicateLimit(@Limit int firstLimit, @Limit int secondLimit);

		@Query
		List<String> scalarInvalidComparator(@Param(value = "name", comparator = "!=") String name);

		@Query
		String scalarMultipleParameterAnnotations(@Param("age") @Limit int age);

		@Query
		String scalarParameterQuery(
				@Param(value = "name", comparator = "LIKE", ignoreNull = true) String name,
				@Param(value = "age", comparator = ">=", ignoreNull = true) Integer minAge,
				@Limit int limit,
				@Offset int offset);

		@Query("SELECT `name` FROM {NAME}")
		String scalarString();

	}

	private static Query.Type extractQueryType(final SQLQuery<DummyEntry, ?> query) throws Exception {
		final Field typeField = query.getClass().getDeclaredField("type");
		typeField.setAccessible(true);
		return (Query.Type) typeField.get(query);
	}

	@SuppressWarnings("unchecked")
	private static List<Object> extractQueryValues(final SQLQuery<DummyEntry, ?> query) throws Exception {
		final Field valuesField = query.getClass().getDeclaredField("values");
		valuesField.setAccessible(true);
		return (List<Object>) valuesField.get(query);
	}

	private final BaseProxyDataBaseEntryUtils utils = new BaseProxyDataBaseEntryUtils("mysql");

	private void assertDetectedType(final String methodName, final Query.Type expectedType) throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod(methodName);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Collections.emptyList());

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(expectedType, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery), methodName);
	}

	@Test
	public void buildMethodQueryFunctionDetectsAutoStrategyForScalarReturnTypes() throws Exception {
		this.assertDetectedType("scalarString", Query.Type.FIRST_NULL);
		this.assertDetectedType("optionalScalarString", Query.Type.FIRST_NULL);
		this.assertDetectedType("listScalarString", Query.Type.LIST_EMPTY);
		this.assertDetectedType("primitiveInt", Query.Type.FIRST_THROW);
		this.assertDetectedType("nonNullScalarString", Query.Type.FIRST_THROW);
		this.assertDetectedType("notNullScalarString", Query.Type.FIRST_THROW);
	}

	@Test
	public void buildMethodQueryFunctionDetectsAutoStrategyFromReturnTypeAndAnnotations() throws Exception {
		this.assertDetectedType("defaultEntry", Query.Type.FIRST_NULL);
		this.assertDetectedType("nullableEntry", Query.Type.FIRST_NULL);
		this.assertDetectedType("optionalEntry", Query.Type.FIRST_NULL);
		this.assertDetectedType("listEntry", Query.Type.LIST_EMPTY);
		this.assertDetectedType("nonNullEntry", Query.Type.FIRST_THROW);
		this.assertDetectedType("notNullEntry", Query.Type.FIRST_THROW);
	}

	@Test
	public void buildMethodQueryFunctionKeepsColumnBasedQueryModeWhenColumnsAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("columnBasedQuery", String.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Matti", 10, 20));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ? LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Matti", 10, 20), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsExplicitStrategy() throws Exception {
		this.assertDetectedType("explicitSingleThrow", Query.Type.SINGLE_THROW);
		this.assertDetectedType("explicitSingleNull", Query.Type.SINGLE_NULL);
		this.assertDetectedType("explicitListNullOverridesListDefault", Query.Type.LIST_NULL);
		this.assertDetectedType("explicitListThrowOverridesListDefault", Query.Type.LIST_THROW);
	}

	@Test
	public void buildMethodQueryFunctionKeepsExplicitStrategyForScalarReturnTypes() throws Exception {
		this.assertDetectedType("explicitScalarSingleThrow", Query.Type.SINGLE_THROW);
		this.assertDetectedType("explicitScalarListNull", Query.Type.LIST_NULL);
		this.assertDetectedType("explicitPrimitiveSingleThrow", Query.Type.SINGLE_THROW);
	}

	@Test
	public void buildMethodQueryFunctionKeepsNullParametersByDefault() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQueryKeepingNull", String.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList((Object) null));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList((Object) null), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsScalarColumnBasedQueryModeWhenColumnsAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("scalarColumnBasedQuery", String.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Matti", 10, 20));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ? LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Matti", 10, 20), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsSqlStableForScalarReturnTypesWhenIgnoreNullIsTrue() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarParameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(null, 18, 5, 0));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE (? IS NULL OR `name` LIKE ?) AND (? IS NULL OR `age` >= ?) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(null, null, 18, 18, 5, 0),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsSqlStableWhenIgnoreNullIsTrue() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(null, 18, 5, 0));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE (? IS NULL OR `name` LIKE ?) AND (? IS NULL OR `age` >= ?) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(null, null, 18, 18, 5, 0),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionRejectsDuplicateLimitParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("duplicateLimit", int.class, int.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsDuplicateLimitParametersForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("scalarDuplicateLimit", int.class, int.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsMultipleQueryParameterAnnotationsOnSameParameter() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("multipleParameterAnnotations", int.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsMultipleQueryParameterAnnotationsOnSameScalarParameter() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("scalarMultipleParameterAnnotations", int.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsUnsupportedParameterComparator() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("invalidComparator", String.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsUnsupportedParameterComparatorForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("scalarInvalidComparator", String.class);

		Assertions.assertThrows(RuntimeException.class, () -> this.utils.buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionReplacesTableNamePlaceholder() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("defaultEntry");

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Collections.emptyList());

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people`", table.lastQuery.getPreparedQuerySQL(table));
	}

	@Test
	public void buildMethodQueryFunctionReplacesTableNamePlaceholderForScalarManualQuery() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("scalarString");

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Collections.emptyList());

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT `name` FROM `people`", table.lastQuery.getPreparedQuerySQL(table));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAllAllowedComparators() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class
				.getDeclaredMethod("allComparators", String.class, int.class, int.class, int.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Mat%", 10, 20, 30, 40, 50));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `people` WHERE `name` LIKE ? AND `age` = ? AND `age` < ? AND `age` <= ? AND `age` > ? AND `age` >= ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Mat%", 10, 20, 30, 40, 50),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAllAllowedComparatorsForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarAllComparators", String.class, int.class, int.class, int.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Mat%", 10, 20, 30, 40, 50));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `people` WHERE `name` LIKE ? AND `age` = ? AND `age` < ? AND `age` <= ? AND `age` > ? AND `age` >= ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Mat%", 10, 20, 30, 40, 50),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("%mat%", null, 10, 20));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE (? IS NULL OR `name` LIKE ?) AND (? IS NULL OR `age` >= ?) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("%mat%", "%mat%", null, null, 10, 20),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsLimitAndOffsetWithoutWhereParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("limitedQuery", int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(25, 50));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(25, 50), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsListScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("listScalarParameterQuery", Integer.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(18));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `age` >= ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(18), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsOptionalScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("optionalScalarParameterQuery", String.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Matti"));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Matti"), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsPrimitiveScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("primitiveParameterQuery", String.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Matti"));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Matti"), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_THROW, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsPrimitiveScalarManualQueryWithParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("manualPrimitiveCountByName", String.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("Matti"));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT COUNT(*) FROM `people` WHERE `name` = ?", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("Matti"), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_THROW, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarParameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList("%mat%", null, 10, 20));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE (? IS NULL OR `name` LIKE ?) AND (? IS NULL OR `age` >= ?) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList("%mat%", "%mat%", null, null, 10, 20),
				BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarLimitAndOffsetWithoutWhereParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("limitedScalarQuery", int.class, int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(25, 50));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(25, 50), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarManualQueryWithParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("manualScalarStringByAge", int.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList(18));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT `name` FROM `people` WHERE `age` >= ?", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList(18), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDataBaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionWithOrderBy() throws Exception {
		final CaptureQueryable table = new CaptureQueryable();
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQueryWithOrderBy", String.class);

		final Function<List<Object>, ?> function = this.utils.buildMethodQueryFunction(table, method);
		function.apply(Arrays.asList((Object) null));

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `people` WHERE `name` = ? ORDER BY `name` ASC;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Arrays.asList((Object) null), BaseProxyDataBaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

}
