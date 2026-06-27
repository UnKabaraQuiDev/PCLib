package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.query.NotNull;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Nullable {

	/**
	 * Indicates whether the column is nullable.<br>
	 * If true, the column can contain null values.<br>
	 * If false, the column must have a value.
	 *
	 * @deprecated this may be changed in a future version, to indicate a field that may be null, use
	 *             {@link Nullable}, else {@link NotNull}
	 */
	@Deprecated
	boolean value() default true;

}
