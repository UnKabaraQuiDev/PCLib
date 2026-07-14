import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.annotations.entry.def.DecimalParam;
import lu.kbra.pclib.db.annotations.entry.def.FixedLength;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.annotations.queryable.def.CharacterSet;
import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.FunctionNotFoundException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;

public class BaseDatabaseEntryUtilsTests {

	public class MockDatabaseScanner extends DatabaseScanner {

		public MockDatabaseScanner(final Database database, final Map<String, Object> hints) {
			super(database, hints);
		}

		public MockDatabaseScanner(final Database database) {
			super(database);
		}

		@Override
		public void scanSelfStructure() {
			super.scanSelfStructure();
		}

		@Override
		public void scanLinks() {
			super.scanLinks();
		}

		@Override
		public TableStructure scanSelfTableStructure(
				final AbstractDBTable<?> instance,
				final Map<String, Object> customHints,
				final Class<? extends AbstractDBTable<?>> tableClazz,
				final Map<String, Object> customEntryHints) {
			return super.scanSelfTableStructure(instance, customHints, tableClazz, customEntryHints);
		}

		@Override
		public void registerSimpleNames(
				final Class<? extends SQLQueryable<?>> tableClazz,
				final Map<String, Object> queryableHints,
				final SQLQueryableStructure tableStructure) {
			super.registerSimpleNames(tableClazz, queryableHints, tableStructure);
		}

		@Override
		public ViewStructure scanSelfViewStructure(
				final AbstractDBView<? extends DatabaseEntry> instance,
				final Map<String, Object> customHints,
				final Class<? extends AbstractDBView<? extends DatabaseEntry>> viewClazz,
				final Map<String, Object> customEntryHints) {
			return super.scanSelfViewStructure(instance, customHints, viewClazz, customEntryHints);
		}

		@Override
		public ColumnData[] computeColumnsFor(
				final SQLQueryable<?> table,
				final SQLQueryableStructure tableStructure,
				final Class<? extends DatabaseEntry> entryClazz) {
			return super.computeColumnsFor(table, tableStructure, entryClazz);
		}

		@Override
		public Field findField(final Class<?> type, final String name) throws NoSuchFieldException {
			return super.findField(type, name);
		}

		@Override
		public Field[] getAllFields(final Class<?> type) {
			return super.getAllFields(type);
		}

	}

	@Data
	@NoArgsConstructor
	private static final class DummyEntry implements DatabaseEntry {

		@Column
		protected String onlyField;

		@Column(name = "manually_renamed_very_really_wow")
		protected String manualField;

		@Override
		public DummyEntry clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	@Getter
	private static final class DummyQueryable implements SQLQueryable<DummyEntry> {

		private final DatabaseEntryUtils databaseEntryUtils;
		private final DummyStructure structure;
		private final Database database;

		public DummyQueryable(final DatabaseEntryUtils utils) {
			this.databaseEntryUtils = utils;
			this.structure = new DummyStructure(utils, DummyQueryable.class, DummyEntry.class);
			this.database = new Database(new MySQLDatabaseConnector("username", "password", "host", 1234), "dummy_database", utils);
			this.database.setDatabaseStructure(new DatabaseStructure("dummy_database",
					utils.getStructureVisitor().qualifiedName("dummy_database"),
					this.database.getCustomHints(),
					null));
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) {
			throw new UnsupportedOperationException();
		}

	}

	@TableConfig(charset = "configured_charset", tableName = "configured_table")
	public static class ConfiguredTableHint {

	}

	@DbmsTableConfig("mysql_table_name")
	public static class DbmsConfiguredTableHint {

	}

	@Documented
	@Retention(RUNTIME)
	@Target({ PARAMETER, METHOD, TYPE_USE })
	public @interface DbmsLengthParam {

		@TypeHint(type = DefaultTypeHints.FIXED_LENGTH, dbms = "postgresql")
		@TypeHint(type = DefaultTypeHints.MAX_LENGTH, dbms = "mysql")
		int value();

	}

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface DbmsTableConfig {

		@QueryableHint(type = DefaultQueryableHints.CHARACTER_SET, dbms = "postgresql")
		@QueryableHint(type = DefaultQueryableHints.NAME_OVERRIDE, dbms = "mysql")
		String value();

	}

	@Documented
	@Retention(RUNTIME)
	@Target(FIELD)
	@DefaultValue(dbms = "postgresql", value = "postgresql")
	@DefaultValue(dbms = "mysql", value = "mysql")
	@DefaultValue(dbms = "sqlite", value = "sqlite")
	public static @interface MetaAnnotated {

	}

	@CharacterSet("charset")
	@NameOverride(":3")
	public static class MultipleMetaTableHint {

	}

	@QueryableHint(type = DefaultQueryableHints.CHARACTER_SET, value = "charset")
	@QueryableHint(type = "yesyesnonohint", value = "maybe")
	public static class MultipleTableHint {

	}

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	@QueryableHint(type = DefaultQueryableHints.CHARACTER_SET, value = "mysql_charset")
	@QueryableHint(type = DefaultQueryableHints.NAME_OVERRIDE, value = "mysql_name")
	public @interface MysqlTableHints {}

	@CharacterSet("charset")
	public static class OneMetaTableHint {

	}

	@QueryableHint(type = DefaultQueryableHints.CHARACTER_SET, value = "charset")
	public static class OneTableHint {

	}

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface TableConfig {

		@QueryableHint(type = DefaultQueryableHints.CHARACTER_SET)
		String charset();

		@QueryableHint(type = DefaultQueryableHints.NAME_OVERRIDE)
		String tableName();

	}

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) String oneAnnotation;

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) @TypeHint(
			value = "155",
			type = DefaultTypeHints.MAX_LENGTH
	) String multipleAnnotations;

	private @FixedLength(value = 12) String oneMetaAnnotation;

	private @FixedLength(value = 12) @MaxLength(13) String multipleMetaAnnotations;

	private @DecimalParam(precision = 10, scale = 2) String decimalParam;

	private @DbmsLengthParam(255) String dbmsLengthParam;

	@DefaultValue("test")
	private String oneDefaultValue;

	@DefaultValue("{NAME}")
	private String onePlaceHolderValue;

	@DefaultValue("{FIELD}")
	private String fieldPlaceHolderValue;

	@DefaultValue("test")
	@DefaultValue("test2")
	private String multipleDefaultValue;

	@DefaultValue("catch all")
	@DefaultValue(value = "specific", dbms = "mysql")
	private String specificMultipleDefaultValue;

	@MetaAnnotated
	private String metaAnnotatedDefaultValue;

//	@MetaAnnotated
//	@DefaultValue(value = "specific", dbms = "mysql")
//	private String metaAnnotatedSpecificDefaultValue;

	@MetaAnnotated
	@DefaultValue(value = "specific", dbms = "mysql")
	@DefaultValue(value = "specific2", dbms = "mysql")
	private String metaAnnotatedMultipleSpecificDefaultValue;

	@DefaultValue(dbms = "postgresql", value = "postgresql")
	@DefaultValue(dbms = "other", value = "other")
	@DefaultValue(dbms = "sqlite", value = "sqlite")
	private String noMatchingDefaultValue;

	@DefaultValue(dbms = "postgresql", value = "postgresql")
	@DefaultValue(dbms = "other", value = "other")
	@DefaultValue(dbms = "sqlite", value = "sqlite")
	@DefaultValue(DefaultValue.NONE)
	private String noMatchingButOneDefaultValue;

//	@Test
//	public <B extends SQLQueryable<T>, T extends DatabaseEntry> void testDefaultValueAnnotations() throws NoSuchFieldException {
//		final DummyQueryable dummy = new DummyQueryable(new BaseDatabaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
//		final DatabaseEntryUtils utils = dummy.getDatabaseEntryUtils();
//
//		Assertions.assertEquals("test", computeDefaultValue(dummy, this.getClass().getDeclaredField("oneDefaultValue")));
//
//		Assertions.assertEquals("`dummy_queryable`",
//				utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("onePlaceHolderValue")));
//
//		Assertions.assertEquals("`field_place_holder_value`",
//				utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("fieldPlaceHolderValue")));
//
//		Assertions.assertThrowsExactly(DBException.class,
//				() -> utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("multipleDefaultValue")));
//
//		Assertions.assertEquals("specific",
//				utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("specificMultipleDefaultValue")));
//
//		Assertions.assertEquals("mysql", utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("metaAnnotatedDefaultValue")));
//
//		Assertions.assertEquals("specific",  utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("metaAnnotatedSpecificDefaultValue")));
//
//		Assertions.assertThrowsExactly(DBException.class,
//				() -> utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("metaAnnotatedMultipleSpecificDefaultValue")));
//
//		Assertions.assertNull(utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("noMatchingDefaultValue")));
//
//		Assertions.assertNull(utils.computeDefaultValue(dummy, this.getClass().getDeclaredField("noMatchingButOneDefaultValue")));
//	}

	@Test
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> void testQueryableHintAnnotations() throws NoSuchFieldException {
		final DummyQueryable dummy = new DummyQueryable(new BaseDatabaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
		final DatabaseEntryUtils utils = dummy.getDatabaseEntryUtils();

		Assertions.assertEquals("charset",
				utils.getHintScanner().computeQueryableHints(OneTableHint.class).get(DefaultQueryableHints.CHARACTER_SET));

		Assertions.assertEquals("charset",
				utils.getHintScanner().computeQueryableHints(OneMetaTableHint.class).get(DefaultQueryableHints.CHARACTER_SET));

		{
			final Map<String, Object> map = utils.getHintScanner().computeQueryableHints(MultipleTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("charset", map.get(DefaultQueryableHints.CHARACTER_SET));
			Assertions.assertEquals("maybe", map.get("yesyesnonohint"));
		}

		{
			final Map<String, Object> map = utils.getHintScanner().computeQueryableHints(MultipleMetaTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("charset", map.get(DefaultQueryableHints.CHARACTER_SET));
			Assertions.assertEquals(":3", map.get(DefaultQueryableHints.NAME_OVERRIDE));
		}

		{
			final Map<String, Object> map = utils.getHintScanner().computeQueryableHints(ConfiguredTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("configured_charset", map.get(DefaultQueryableHints.CHARACTER_SET));
			Assertions.assertEquals("configured_table", map.get(DefaultQueryableHints.NAME_OVERRIDE));
		}

		{
			final Map<String, Object> map = utils.getHintScanner().computeQueryableHints(DbmsConfiguredTableHint.class);
			System.err.println(map);
			Assertions.assertFalse(map.containsKey(DefaultQueryableHints.CHARACTER_SET));
			Assertions.assertEquals("mysql_table_name", map.get(DefaultQueryableHints.NAME_OVERRIDE));
		}
	}

	@Test
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> void testTypeHintAnnotations() throws NoSuchFieldException {
		final DummyQueryable dummy = new DummyQueryable(new BaseDatabaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
		@SuppressWarnings("unchecked") final Class<B> clazz = (Class<B>) dummy.getTargetClass();
		final DatabaseEntryUtils utils = dummy.getDatabaseEntryUtils();

		Assertions.assertEquals("12",
				utils.getHintScanner()
						.computeTypeHints(this.getClass().getDeclaredField("oneAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils.getHintScanner()
					.computeTypeHints(this.getClass().getDeclaredField("multipleAnnotations").getAnnotatedType());
			Assertions.assertEquals("12", map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals("155", map.get(DefaultTypeHints.MAX_LENGTH));
		}

		Assertions.assertEquals(12,
				utils.getHintScanner()
						.computeTypeHints(this.getClass().getDeclaredField("oneMetaAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils.getHintScanner()
					.computeTypeHints(this.getClass().getDeclaredField("multipleMetaAnnotations").getAnnotatedType());
			Assertions.assertEquals(12, map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals(13, map.get(DefaultTypeHints.MAX_LENGTH));
		}

		{
			final Map<String, Object> map = utils.getHintScanner()
					.computeTypeHints(this.getClass().getDeclaredField("decimalParam").getAnnotatedType());

			Assertions.assertEquals(10, map.get(DefaultTypeHints.PRECISION));
			Assertions.assertEquals(2, map.get(DefaultTypeHints.SCALE));
		}

		{
			final Map<String, Object> map = utils.getHintScanner()
					.computeTypeHints(this.getClass().getDeclaredField("dbmsLengthParam").getAnnotatedType());

			Assertions.assertFalse(map.containsKey(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals(255, map.get(DefaultTypeHints.MAX_LENGTH));
		}
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
					MySQLDbmsProvider.DBMS_QUALIFIER_NAME,
					SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
					PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME }
	)
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> void processQualifiedNames(final String input) {
		final DummyQueryable dummy = new DummyQueryable(new BaseDatabaseEntryUtils(input));
		dummy.getStructure().getHints().put(DefaultQueryableHints.DEFINED_NAME, "definedName");
		final DatabaseEntryUtils utils = dummy.getDatabaseEntryUtils();
		new MockDatabaseScanner(dummy.getDatabase())
				.registerSimpleNames(dummy.getTargetClass(), Collections.emptyMap(), dummy.getStructure());
		((DummyStructure) dummy.getStructure()).setColumns(new MockDatabaseScanner(dummy.getDatabase())
				.computeColumnsFor(dummy, dummy.getStructure(), dummy.getStructure().getEntryClass()));
//		System.err.println(Arrays.asList(dummy.getStructure().getColumns()));
//		System.err.println(input + " " + utils.getDbmsQualifierName() + " " + utils.getStructureVisitor().getClass().getSimpleName());

		Assertions.assertEquals(utils.getStructureVisitor().qualifiedName("name"), utils.replaceSQLQualifiers(dummy, "{Q:name}"));
		Assertions.assertEquals("count", utils.replaceSQLQualifiers(dummy, "{F:count}").toLowerCase());
		Assertions.assertThrows(FunctionNotFoundException.class,
				() -> utils.replaceSQLQualifiers(dummy, "{F:surelythisdoesn'texist hehehe}"));
		Assertions.assertEquals(
				utils.getStructureVisitor().qualifiedName(PCUtils.appendArrays(dummy.getStructure().getNameParts(), "only_field")),
				utils.replaceSQLQualifiers(dummy, "{M:onlyField}"));
		Assertions.assertEquals(
				utils.getStructureVisitor()
						.qualifiedName(PCUtils.appendArrays(dummy.getStructure().getNameParts(), "manually_renamed_very_really_wow")),
				utils.replaceSQLQualifiers(dummy, "{M:manualField}"));
	}

}
