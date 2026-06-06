import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.meta.FixedLength;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.autobuild.column.type.meta.TypeHint;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;

public class BaseDataBaseEntryUtilsTests {

	private final BaseProxyDataBaseEntryUtils utils = new BaseProxyDataBaseEntryUtils();

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) String oneAnnotation;
	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) @TypeHint(
			value = "155",
			type = DefaultTypeHints.MAX_LENGTH
	) String multipleAnnotations;

	private @FixedLength(value = 12) String oneMetaAnnotation;
	private @FixedLength(value = 12) @MaxLength(13) String multipleMetaAnnotations;

	@Test
	public void testTypeHintAnnotations() throws NoSuchFieldException {
		assertEquals("12",
				utils.getTypeHints(getClass().getDeclaredField("oneAnnotation").getAnnotatedType()).get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils.getTypeHints(getClass().getDeclaredField("multipleAnnotations").getAnnotatedType());
			assertEquals("12", map.get(DefaultTypeHints.FIXED_LENGTH));
			assertEquals("155", map.get(DefaultTypeHints.MAX_LENGTH));
		}

		assertEquals(12,
				utils.getTypeHints(getClass().getDeclaredField("oneMetaAnnotation").getAnnotatedType()).get(DefaultTypeHints.FIXED_LENGTH));

		{
			final Map<String, Object> map = utils.getTypeHints(getClass().getDeclaredField("multipleMetaAnnotations").getAnnotatedType());
			assertEquals(12, map.get(DefaultTypeHints.FIXED_LENGTH));
			assertEquals(13, map.get(DefaultTypeHints.MAX_LENGTH));
		}
	}

}
