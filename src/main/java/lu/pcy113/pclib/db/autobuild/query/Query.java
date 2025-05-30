package lu.pcy113.pclib.db.autobuild.query;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.pcy113.pclib.db.annotations.view.OrderBy;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Query {

	public static final String OFFSET_KEY = "_offset", LIMIT_KEY = "_limit", TABLE_NAME = "{NAME}";

	public static enum Type {
		AUTO,

		FIRST_THROW,
		FIRST_NULL,

		SINGLE_THROW,
		SINGLE_NULL,

		LIST_NULL,
		LIST_THROW,
		LIST_EMPTY;
	}

	String value() default "";

	String[] columns() default {};

	Type strategy() default Type.AUTO;

	/**
	 * {@code others offset limit}
	 */
	int limit() default -1;

	/**
	 * {@code others offset limit}
	 */
	int offset() default -1;

	OrderBy[] orderBy() default {};

}
