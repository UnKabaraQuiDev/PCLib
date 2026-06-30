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
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lombok.Data;
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
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.FunctionNotFoundException;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public class BaseDataBaseEntryUtilsTests {

	@Data
	@NoArgsConstructor
	private static final class DummyEntry implements DataBaseEntry {

		@Column
		protected String onlyField;

		@Column(name = "manually_renamed_very_really_wow")
		protected String manualField;

		@Override
		public DummyEntry clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	private static final class DummyQueryable implements SQLQueryable<DummyEntry> {

		private final DataBaseEntryUtils utils;

		public DummyQueryable(final DataBaseEntryUtils utils) {
			this.utils = utils;
		}

		private DummyQueryable(final String protocol) {
			this.utils = new BaseDataBaseEntryUtils(protocol);
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public DataBaseEntryUtils getDataBaseEntryUtils() {
			return this.utils;
		}

		@Override
		public Class<? extends SQLQueryable<DummyEntry>> getTargetClass() {
			return this.getClass();
		}

		@Override
		public <B> B query(final SQLQuery<DummyEntry, B> query) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<? extends DummyEntry> getEntryClass() {
			return DummyEntry.class;
		}

		@Override
		public DataBase getDatabase() {
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

		@QueryableHint(type = DefaultTableHints.CHARACTER_SET, dbms = "postgresql")
		@QueryableHint(type = DefaultTableHints.NAME_OVERRIDE, dbms = "mysql")
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

	@QueryableHint(type = DefaultTableHints.CHARACTER_SET, value = "charset")
	@QueryableHint(type = "yesyesnonohint", value = "maybe")
	public static class MultipleTableHint {

	}

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	@QueryableHint(type = DefaultTableHints.CHARACTER_SET, value = "mysql_charset")
	@QueryableHint(type = DefaultTableHints.NAME_OVERRIDE, value = "mysql_name")
	public @interface MysqlTableHints {
	}

	@CharacterSet("charset")
	public static class OneMetaTableHint {

	}

	@QueryableHint(type = DefaultTableHints.CHARACTER_SET, value = "charset")
	public static class OneTableHint {

	}

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface TableConfig {

		@QueryableHint(type = DefaultTableHints.CHARACTER_SET)
		String charset();

		@QueryableHint(type = DefaultTableHints.NAME_OVERRIDE)
		String tableName();

	}

	public class TestBaseDataBaseEntryUtils extends BaseDataBaseEntryUtils {

		public TestBaseDataBaseEntryUtils(final String protocol) {
			super(protocol);
		}

		@Override
		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String computeDefaultValue(Class<B> clazz,
				Field field) {
			return super.computeDefaultValue(clazz, field);
		}

		public void setDbmsQualifierName(final String d) {
			super.dbmsQualifierName = d;
		}

	}

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) String oneAnnotation;

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) @TypeHint(value = "155", type = DefaultTypeHints.MAX_LENGTH) String multipleAnnotations;

	private @FixedLength(value = 12) String oneMetaAnnotation;

	private @FixedLength(value = 12) @MaxLength(13) String multipleMetaAnnotations;

	private @DecimalParam(precision = 10, scale = 2) String decimalParam;

	private @DbmsLengthParam(255) String dbmsLengthParam;

	@DefaultValue("test")
	private String oneDefaultValue;

	@DefaultValue("{NAME}")
	private String onePlaceHolderValue;

	@DefaultValue("test")
	@DefaultValue("test2")
	private String multipleDefaultValue;

	@DefaultValue("catch all")
	@DefaultValue(value = "specific", dbms = "mysql")
	private String specificMultipleDefaultValue;

	@MetaAnnotated
	private String metaAnnotatedDefaultValue;

	@MetaAnnotated
	@DefaultValue(value = "specific", dbms = "mysql")
	private String metaAnnotatedSpecificDefaultValue;

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

	@Test
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void testDefaultValueAnnotations()
			throws NoSuchFieldException {
		final DummyQueryable dummy = new DummyQueryable(
				new BaseDataBaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
		@SuppressWarnings("unchecked")
		final Class<B> clazz = (Class<B>) dummy.getTargetClass();
		final DataBaseEntryUtils utils = dummy.getDataBaseEntryUtils();

		Assertions.assertEquals("test",
				utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("oneDefaultValue")));

		Assertions.assertEquals("`dummy_queryable`",
				utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("onePlaceHolderValue")));

		Assertions.assertThrowsExactly(DBException.class,
				() -> utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("multipleDefaultValue")));

		Assertions.assertEquals("specific",
				utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("specificMultipleDefaultValue")));

		Assertions.assertEquals("mysql",
				utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("metaAnnotatedDefaultValue")));

		Assertions.assertEquals("specific", utils.computeDefaultValue(clazz,
				this.getClass().getDeclaredField("metaAnnotatedSpecificDefaultValue")));

		Assertions.assertThrowsExactly(DBException.class, () -> utils.computeDefaultValue(clazz,
				this.getClass().getDeclaredField("metaAnnotatedMultipleSpecificDefaultValue")));

		Assertions.assertThrowsExactly(DBException.class,
				() -> utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("noMatchingDefaultValue")));

		Assertions.assertNull(
				utils.computeDefaultValue(clazz, this.getClass().getDeclaredField("noMatchingButOneDefaultValue")));
	}

	@Test
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void testQueryableHintAnnotations()
			throws NoSuchFieldException {
		final DummyQueryable dummy = new DummyQueryable(
				new BaseDataBaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
		@SuppressWarnings("unchecked")
		final Class<B> clazz = (Class<B>) dummy.getTargetClass();
		final DataBaseEntryUtils utils = dummy.getDataBaseEntryUtils();

		Assertions.assertEquals("charset",
				utils.getQueryableHints(OneTableHint.class).get(DefaultTableHints.CHARACTER_SET));

		Assertions.assertEquals("charset",
				utils.getQueryableHints(OneMetaTableHint.class).get(DefaultTableHints.CHARACTER_SET));

		{
			final Map<String, Object> map = utils.getQueryableHints(MultipleTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("charset", map.get(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals("maybe", map.get("yesyesnonohint"));
		}

		{
			final Map<String, Object> map = utils.getQueryableHints(MultipleMetaTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("charset", map.get(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals(":3", map.get(DefaultTableHints.NAME_OVERRIDE));
		}

		{
			final Map<String, Object> map = utils.getQueryableHints(ConfiguredTableHint.class);
			System.err.println(map);
			Assertions.assertEquals("configured_charset", map.get(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals("configured_table", map.get(DefaultTableHints.NAME_OVERRIDE));
		}

		{
			final Map<String, Object> map = utils.getQueryableHints(DbmsConfiguredTableHint.class);
			System.err.println(map);
			Assertions.assertFalse(map.containsKey(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals("mysql_table_name", map.get(DefaultTableHints.NAME_OVERRIDE));
		}
	}

	@Test
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void testTypeHintAnnotations()
			throws NoSuchFieldException {
		final DummyQueryable dummy = new DummyQueryable(
				new BaseDataBaseEntryUtils(MySQLDbmsProvider.DBMS_QUALIFIER_NAME));
		@SuppressWarnings("unchecked")
		final Class<B> clazz = (Class<B>) dummy.getTargetClass();
		final DataBaseEntryUtils utils = dummy.getDataBaseEntryUtils();

		Assertions.assertEquals("12",
				utils.getTypeHints(this.getClass().getDeclaredField("oneAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils
					.getTypeHints(this.getClass().getDeclaredField("multipleAnnotations").getAnnotatedType());
			Assertions.assertEquals("12", map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals("155", map.get(DefaultTypeHints.MAX_LENGTH));
		}

		Assertions.assertEquals(12,
				utils.getTypeHints(this.getClass().getDeclaredField("oneMetaAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils
					.getTypeHints(this.getClass().getDeclaredField("multipleMetaAnnotations").getAnnotatedType());
			Assertions.assertEquals(12, map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals(13, map.get(DefaultTypeHints.MAX_LENGTH));
		}

		{
			final Map<String, Object> map = utils
					.getTypeHints(this.getClass().getDeclaredField("decimalParam").getAnnotatedType());

			Assertions.assertEquals(10, map.get(DefaultTypeHints.PRECISION));
			Assertions.assertEquals(2, map.get(DefaultTypeHints.SCALE));
		}

		{
			final Map<String, Object> map = utils
					.getTypeHints(this.getClass().getDeclaredField("dbmsLengthParam").getAnnotatedType());

			Assertions.assertFalse(map.containsKey(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals(255, map.get(DefaultTypeHints.MAX_LENGTH));
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { MySQLDbmsProvider.DBMS_QUALIFIER_NAME, SQLiteDbmsProvider.DBMS_QUALIFIER_NAME,
			PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME })
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void processQualifiedNames(final String input) {
		final DummyQueryable dummy = new DummyQueryable(new BaseDataBaseEntryUtils(input));
		@SuppressWarnings("unchecked")
		final Class<B> clazz = (Class<B>) dummy.getTargetClass();
		final DataBaseEntryUtils utils = dummy.getDataBaseEntryUtils();

		Assertions.assertEquals(SQLStructureVisitors.forProtocol(input).qualifiedName("name"),
				utils.replaceSQLQualifiers(clazz, "{Q:name}"));
		Assertions.assertEquals("count", utils.replaceSQLQualifiers(clazz, "{F:count}").toLowerCase());
		Assertions.assertThrows(FunctionNotFoundException.class,
				() -> utils.replaceSQLQualifiers(clazz, "{F:surelythisdoesn'texist hehehe}"));
		Assertions.assertEquals(utils.getStructureVisitor().qualifiedName("only_field"),
				utils.replaceSQLQualifiers(clazz, "{M:onlyField}"));
		Assertions.assertEquals(utils.getStructureVisitor().qualifiedName("manually_renamed_very_really_wow"),
				utils.replaceSQLQualifiers(clazz, "{M:manualField}"));
	}

}
