package lu.pcy113.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Nullable {

	/**
	 * Indicates whether the column is nullable.<br>
	 * If true, the column can contain null values.<br>
	 * If false, the column must have a value.
	 */
	boolean value() default true;
}
