import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.Offset;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.OrderBy.Type;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.BaseProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BaseProxyDatabaseEntryUtilsTests {

	@Getter
	private static final class CaptureQueryable implements SQLQueryable<DummyEntry> {

		private SQLQuery<DummyEntry, ?> lastQuery;
		private final DatabaseEntryUtils databaseEntryUtils;
		private final DatabaseConnector connector = new MySQLDatabaseConnector(null, null, null, 0);
		private final DummyStructure structure;
		private final Database database;

		public CaptureQueryable(final DatabaseEntryUtils utils) {
			this.databaseEntryUtils = utils;
			this.structure = new DummyStructure(this.databaseEntryUtils, CaptureQueryable.class, DummyEntry.class);
			this.database = new Database(new MySQLDatabaseConnector(), "dummy_database", utils);
			utils.setDatabaseScanner(new DatabaseScanner(this.database));
		}

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
		public SQLQueryableHookManager getQueryableHookManager() {
			return this.databaseEntryUtils.getQueryableHookManager();
		}

	}

	@Getter
	private static final class OtherQueryable implements SQLQueryable<OtherEntry> {

		private SQLQuery<OtherEntry, ?> lastQuery;
		private final DatabaseEntryUtils databaseEntryUtils;
		private final DatabaseConnector connector = new MySQLDatabaseConnector(null, null, null, 0);
		private final DummyStructure structure;
		private final Database database;

		public OtherQueryable(final DatabaseEntryUtils utils) {
			this.databaseEntryUtils = utils;
			this.structure = new DummyStructure(this.databaseEntryUtils, OtherQueryable.class, OtherEntry.class);
			this.database = new Database(new MySQLDatabaseConnector(), "dummy_database", utils);
		}

		@Override
		public int count() throws DBException {
			return 0;
		}

		@Override
		public <B> B query(final SQLQuery<OtherEntry, B> query) throws DBException {
			this.lastQuery = query;
			return null;
		}

		@Override
		public SQLQueryableHookManager getQueryableHookManager() {
			return this.databaseEntryUtils.getQueryableHookManager();
		}

	}

	@Data
	@NoArgsConstructor
	private static final class DummyEntry implements DatabaseEntry {

		@Column
		@PrimaryKey
		private String onlyField;

		@Override
		public BaseProxyDatabaseEntryUtilsTests.DummyEntry clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	@Data
	@NoArgsConstructor
	private static final class OtherEntry implements DatabaseEntry {

		@Column
		@PrimaryKey
		@ForeignKey(table = CaptureQueryable.class)
		private String fkField;

		@Override
		public BaseProxyDatabaseEntryUtilsTests.OtherEntry clone() {
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

		@Query(columns = { "name" })
		List<DummyEntry> columnBasedQuery(String name, @Limit int limit, @Offset int offset);

		@Query("SELECT * FROM {NAME}")
		DummyEntry defaultEntry();

		@Query("SELECT * FROM {NAME} WHERE {P:onlyReallyOnlyField} <> {V:onlyReallyOnlyField}")
		DummyEntry paramByField(@Param(member = "onlyField") String onlyReallyOnlyField);

		@Query("SELECT * FROM {NAME} WHERE age < {V:age} AND {P:nameOrSum} <> {V:nameOrSum}")
		DummyEntry paramByShuffledFields(@Param(member = "onlyField") String nameOrSum, @Param int age);

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

		@Query(tables = { @Table(typeName = OtherQueryable.class, on = "a = b") })
		int parametersWithJoin(@Param(value = "otherValue") int otherValue);

		@Query
		List<String> scalarAllComparators(
				@Param(value = "name", comparator = "LIKE") String likeName,
				@Param(value = "age", comparator = "=") int equalAge,
				@Param(value = "age", comparator = "<") int lowerThanAge,
				@Param(value = "age", comparator = "<=") int lowerOrEqualAge,
				@Param(value = "age", comparator = ">") int greaterThanAge,
				@Param(value = "age", comparator = ">=") int greaterOrEqualAge);

		@Query(columns = { "name" })
		List<String> scalarColumnBasedQuery(String name, @Limit int limit, @Offset int offset);

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

	private static Object[] extractQueryValues(final SQLQuery<DummyEntry, ?> query) throws Exception {
		final Field valuesField = query.getClass().getDeclaredField("paramValues");
		valuesField.setAccessible(true);
		return (Object[]) valuesField.get(query);
	}

	private final BaseProxyDatabaseEntryUtils utils = new BaseProxyDatabaseEntryUtils("mysql");

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
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("columnBasedQuery", String.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Matti", 10, 20 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ? LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Matti", 10, 20 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
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
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQueryKeepingNull", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { null });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { null }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsScalarColumnBasedQueryModeWhenColumnsAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("scalarColumnBasedQuery", String.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Matti", 10, 20 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ? LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Matti", 10, 20 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsSqlStableForScalarReturnTypesWhenIgnoreNullIsTrue() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarParameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { null, 18, 5, 0 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE (? IS NULL OR ? LIKE `name`) AND (? IS NULL OR ? >= `age`) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionKeepsSqlStableWhenIgnoreNullIsTrue() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { null, 18, 5, 0 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE (? IS NULL OR ? LIKE `name`) AND (? IS NULL OR ? >= `age`) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { null, 18, 5, 0 }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionRejectsDuplicateLimitParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("duplicateLimit", int.class, int.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsDuplicateLimitParametersForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("scalarDuplicateLimit", int.class, int.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsMultipleQueryParameterAnnotationsOnSameParameter() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("multipleParameterAnnotations", int.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsMultipleQueryParameterAnnotationsOnSameScalarParameter() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("scalarMultipleParameterAnnotations", int.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsUnsupportedParameterComparator() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("invalidComparator", String.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionRejectsUnsupportedParameterComparatorForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("scalarInvalidComparator", String.class);

		Assertions.assertThrows(RuntimeException.class,
				() -> this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method));
	}

	@Test
	public void buildMethodQueryFunctionReplacesTableNamePlaceholder() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("defaultEntry");

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[0]);

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable`", table.lastQuery.getPreparedQuerySQL(table));
	}

	@Test
	public void buildMethodQueryFunctionReplacesParamColumnNameAndValue() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		table.getStructure()
				.setColumns(
						new MockDatabaseScanner(table.getDatabase()).computeColumnsFor(table, table.getStructure(), table.getEntryClass()));
		final Method method = QueryMethods.class.getDeclaredMethod("paramByField", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "string" });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `capture_queryable`.`only_field` <> ?",
				table.lastQuery.getPreparedQuerySQL(table));
	}

	@Test
	public void buildMethodQueryFunctionShufflesParams() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		table.getStructure()
				.setColumns(
						new MockDatabaseScanner(table.getDatabase()).computeColumnsFor(table, table.getStructure(), table.getEntryClass()));
		final Method method = QueryMethods.class.getDeclaredMethod("paramByShuffledFields", String.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "string", 12 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE age < ? AND `capture_queryable`.`only_field` <> ?",
				table.lastQuery.getPreparedQuerySQL(table));
		final Field field = table.lastQuery.getClass().getDeclaredField("reordering");
		field.setAccessible(true);
		Assertions.assertArrayEquals(new int[] { 1, 0 }, (int[]) field.get(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionReplacesTableNamePlaceholderForScalarManualQuery() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("scalarString");

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[0]);

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT `name` FROM `capture_queryable`", table.lastQuery.getPreparedQuerySQL(table));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAllAllowedComparators() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class
				.getDeclaredMethod("allComparators", String.class, int.class, int.class, int.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Mat%", 10, 20, 30, 40, 50 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE `name` LIKE ? AND `age` = ? AND `age` < ? AND `age` <= ? AND `age` > ? AND `age` >= ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Mat%", 10, 20, 30, 40, 50 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAllAllowedComparatorsForScalarReturnTypes() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarAllComparators", String.class, int.class, int.class, int.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Mat%", 10, 20, 30, 40, 50 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE `name` LIKE ? AND `age` = ? AND `age` < ? AND `age` <= ? AND `age` > ? AND `age` >= ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Mat%", 10, 20, 30, 40, 50 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "%mat%", null, 10, 20 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE (? IS NULL OR ? LIKE `name`) AND (? IS NULL OR ? >= `age`) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "%mat%", null, 10, 20 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsLimitAndOffsetWithoutWhereParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("limitedQuery", int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { 25, 50 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { 25, 50 }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsListScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("listScalarParameterQuery", Integer.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { 18 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `age` >= ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { 18 }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsOptionalScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("optionalScalarParameterQuery", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Matti" });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ?;",
				this.normalizeSql(table.lastQuery.getPreparedQuerySQL(table)));
		Assertions.assertArrayEquals(new Object[] { "Matti" }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	private String normalizeSql(final String string) {
		return string.replaceAll("\\s+", " ");
	}

	@Test
	public void buildMethodQueryFunctionSupportsPrimitiveScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("primitiveParameterQuery", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Matti" });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Matti" }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_THROW, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsPrimitiveScalarManualQueryWithParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("manualPrimitiveCountByName", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "Matti" });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT COUNT(*) FROM `capture_queryable` WHERE `name` = ?", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "Matti" }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_THROW, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarAnnotatedParametersWhenNoColumnsOrSqlAreDeclared() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class
				.getDeclaredMethod("scalarParameterQuery", String.class, Integer.class, int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { "%mat%", null, 10, 20 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(
				"SELECT * FROM `capture_queryable` WHERE (? IS NULL OR ? LIKE `name`) AND (? IS NULL OR ? >= `age`) LIMIT ? OFFSET ?;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { "%mat%", null, 10, 20 },
				BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarLimitAndOffsetWithoutWhereParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("limitedScalarQuery", int.class, int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { 25, 50 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` LIMIT ? OFFSET ?;", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { 25, 50 }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.LIST_EMPTY, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionSupportsScalarManualQueryWithParameters() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("manualScalarStringByAge", int.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { 18 });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT `name` FROM `capture_queryable` WHERE `age` >= ?", table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { 18 }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
		Assertions.assertEquals(Query.Type.FIRST_NULL, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery));
	}

	@Test
	public void buildMethodQueryFunctionWithOrderBy() throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod("parameterQueryWithOrderBy", String.class);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[] { null });

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals("SELECT * FROM `capture_queryable` WHERE `name` = ? ORDER BY `name` ASC;",
				table.lastQuery.getPreparedQuerySQL(table));
		Assertions.assertArrayEquals(new Object[] { null }, BaseProxyDatabaseEntryUtilsTests.extractQueryValues(table.lastQuery));
	}

	private void assertDetectedType(final String methodName, final Query.Type expectedType) throws Exception {
		final CaptureQueryable table = new CaptureQueryable(this.utils);
		final Method method = QueryMethods.class.getDeclaredMethod(methodName);

		final Function<Object[], ?> function = this.utils.getQueryFunctionProvider().buildMethodQueryFunction(table, method);
		function.apply(new Object[0]);

		Assertions.assertNotNull(table.lastQuery);
		Assertions.assertEquals(expectedType, BaseProxyDatabaseEntryUtilsTests.extractQueryType(table.lastQuery), methodName);
	}

}
