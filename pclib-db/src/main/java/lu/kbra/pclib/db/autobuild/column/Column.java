package lu.kbra.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface Column {

	String name() default "";

	Class<?> type() default Class.class;

	/**
	 * @deprecated Use {@link MaxLength} instead.
	 */
	@Deprecated
	int length() default -1;

	/**
	 * @deprecated completely ignored since v1.1.0(-SNAPSHOT)
	 */
	@Deprecated
	String[] params() default {};

}
