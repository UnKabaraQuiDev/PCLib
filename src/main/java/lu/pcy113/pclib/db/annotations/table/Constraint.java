package lu.pcy113.pclib.db.annotations.table;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {

	public enum Type {
		PRIMARY_KEY,
		FOREIGN_KEY,
		UNIQUE,
		CHECK,
		INDEX;
	}

	public enum OnEvent {
		CASCADE,
		SET_NULL("SET NULL"),
		RESTRICT,
		NO_ACTION("NO ACTION");

		private final String sql;

		private OnEvent() {
			this.sql = name();
		}

		private OnEvent(String sql) {
			this.sql = sql;
		}

		@Override
		public String toString() {
			return this.sql;
		}

	}

	Type type();

	String name();

	// -- foreign
	String referenceTable() default "";
	
	Class<?> referenceTableType() default Class.class;

	String referenceColumn() default "";

	OnEvent onDelete() default OnEvent.NO_ACTION;

	OnEvent onUpdate() default OnEvent.NO_ACTION;

	// -- index
	// -- unique
	// -- primary key
	// -- foreign
	String[] columns() default {};

	// -- check
	String check() default "";

}
