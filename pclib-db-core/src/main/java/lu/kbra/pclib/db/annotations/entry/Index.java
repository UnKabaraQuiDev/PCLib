package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(Indices.class)
@ColumnHint(type = DefaultColumnHints.INDEX, repeatable = true, grouped = true)
public @interface Index {

	/**
	 * Keys with the same value are considered grouped.
	 */
	@ColumnHint(type = DefaultColumnHints.INDEX_INDEX)
	int value() default 0;

	@ColumnHint(type = DefaultColumnHints.INDEX_NAME)
	String name() default "";

}
