import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.meta.FixedLength;
import lu.kbra.pclib.db.autobuild.column.meta.MaxLength;
import lu.kbra.pclib.db.autobuild.column.meta.TypeHint;
import lu.kbra.pclib.db.autobuild.table.meta.CharacterSet;
import lu.kbra.pclib.db.autobuild.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.autobuild.table.meta.TableHint;
import lu.kbra.pclib.db.autobuild.table.meta.TableName;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;

public class BaseDataBaseEntryUtilsTests {

	@CharacterSet("charset")
	@TableName(":3")
	public static class MultipleMetaTableHint {

	}

	@TableHint(type = DefaultTableHints.CHARACTER_SET, value = "charset")
	@TableHint(type = "yesyesnonohint", value = "maybe")
	public static class MultipleTableHint {

	}

	@CharacterSet("charset")
	public static class OneMetaTableHint {

	}

	@TableHint(type = DefaultTableHints.CHARACTER_SET, value = "charset")
	public static class OneTableHint {

	}

	private final BaseProxyDataBaseEntryUtils utils = new BaseProxyDataBaseEntryUtils("mysql");

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) String oneAnnotation;

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) @TypeHint(
			value = "155",
			type = DefaultTypeHints.MAX_LENGTH
	) String multipleAnnotations;

	private @FixedLength(value = 12) String oneMetaAnnotation;

	private @FixedLength(value = 12) @MaxLength(13) String multipleMetaAnnotations;

	@Test
	public void testTableHintAnnotations() throws NoSuchFieldException {
		Assertions.assertEquals("charset", this.utils.getQueryableHints(OneTableHint.class).get(DefaultTableHints.CHARACTER_SET));

		Assertions.assertEquals("charset", this.utils.getQueryableHints(OneMetaTableHint.class).get(DefaultTableHints.CHARACTER_SET));

		{
			final Map<String, Object> map = this.utils.getQueryableHints(MultipleTableHint.class);
			Assertions.assertEquals("charset", map.get(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals("maybe", map.get("yesyesnonohint"));
		}

		{
			final Map<String, Object> map = this.utils.getQueryableHints(MultipleMetaTableHint.class);
			Assertions.assertEquals("charset", map.get(DefaultTableHints.CHARACTER_SET));
			Assertions.assertEquals(":3", map.get(DefaultTableHints.NAME_OVERRIDE));
		}
	}

	@Test
	public void testTypeHintAnnotations() throws NoSuchFieldException {
		Assertions.assertEquals("12",
				this.utils.getTypeHints(this.getClass().getDeclaredField("oneAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = this.utils
					.getTypeHints(this.getClass().getDeclaredField("multipleAnnotations").getAnnotatedType());
			Assertions.assertEquals("12", map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals("155", map.get(DefaultTypeHints.MAX_LENGTH));
		}

		Assertions.assertEquals(12,
				this.utils.getTypeHints(this.getClass().getDeclaredField("oneMetaAnnotation").getAnnotatedType())
						.get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = this.utils
					.getTypeHints(this.getClass().getDeclaredField("multipleMetaAnnotations").getAnnotatedType());
			Assertions.assertEquals(12, map.get(DefaultTypeHints.FIXED_LENGTH));
			Assertions.assertEquals(13, map.get(DefaultTypeHints.MAX_LENGTH));
		}
	}

}
