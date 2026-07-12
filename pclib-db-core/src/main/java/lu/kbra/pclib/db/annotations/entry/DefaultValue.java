package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE })
@Repeatable(DefaultValues.class)
public @interface DefaultValue {

	String NONE = "{NONE}";
	String NULL = "NULL";
	String I_KNOW = "{I KNOW but let me be silly :3}";

	/**
	 * glob pattern
	 */
	@DbmsFilter
	String dbms() default "";

	@ColumnHint(type = DefaultColumnHints.DEFAULT_VALUE)
	@ColumnHint(type = DefaultColumnHints.GENERATED_VALUE)
	String value();

}
