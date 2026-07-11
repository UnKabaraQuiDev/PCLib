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
@Repeatable(Uniques.class)
@ColumnHint(type = DefaultColumnHints.UNIQUE, repeatable = true, grouped = true)
public @interface Unique {

	/**
	 * Keys with the same value are considered grouped.
	 */
	@ColumnHint(type = DefaultColumnHints.UNIQUE_INDEX)
	int value() default 0;

}
