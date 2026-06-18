import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.meta.FixedLength;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.autobuild.column.type.meta.TypeHint;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;

public class BaseDataBaseEntryUtilsTests {

	private final BaseProxyDataBaseEntryUtils utils = new BaseProxyDataBaseEntryUtils("mysql");

	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) String oneAnnotation;
	private @TypeHint(value = "12", type = DefaultTypeHints.FIXED_LENGTH) @TypeHint(
			value = "155",
			type = DefaultTypeHints.MAX_LENGTH
	) String multipleAnnotations;

	private @FixedLength(value = 12) String oneMetaAnnotation;
	private @FixedLength(value = 12) @MaxLength(13) String multipleMetaAnnotations;

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
