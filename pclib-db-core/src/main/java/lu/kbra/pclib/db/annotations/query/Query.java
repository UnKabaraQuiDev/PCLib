package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.view.OrderBy;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Query {

	public static enum Type {
		AUTO,

		FIRST_THROW,
		FIRST_NULL,

		SINGLE_THROW,
		SINGLE_NULL,

		LIST_THROW,
		LIST_NULL,
		LIST_EMPTY;

		Type() {
		}

		public boolean isAuto() {
			return this == AUTO;
		}

		public boolean isEmpty() {
			switch (this) {
			case LIST_EMPTY:
				return true;
			default:
				return false;
			}
		}

		public boolean isFirst() {
			return this == FIRST_THROW || this == FIRST_NULL;
		}

		public boolean isList() {
			return this == LIST_EMPTY || this == LIST_THROW || this == LIST_NULL;
		}

		public boolean isNullable() {
			switch (this) {
			case FIRST_NULL:
			case LIST_NULL:
			case SINGLE_NULL:
				return true;
			default:
				return false;
			}
		}

		public boolean isSingle() {
			return this == SINGLE_THROW || this == SINGLE_NULL;
		}

		public boolean isThrowing() {
			switch (this) {
			case FIRST_THROW:
			case LIST_THROW:
			case SINGLE_THROW:
				return true;
			default:
				return false;
			}
		}
	}

	public static final String OFFSET_KEY = "_offset";
	public static final String LIMIT_KEY = "_limit";

	public static final String TABLE_NAME = "{NAME}";

	String[] columns() default {};

	/**
	 * {@code others offset limit}
	 */
	int limit() default -1;

	/**
	 * {@code others offset limit}
	 */
	int offset() default -1;

	OrderBy[] orderBy() default {};

	Type strategy() default Type.AUTO;

	String value() default "";

}
