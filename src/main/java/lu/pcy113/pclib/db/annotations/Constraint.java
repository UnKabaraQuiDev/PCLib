package lu.pcy113.pclib.db.annotations;

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
		CHECK;
	}

	public enum OnEvent {
		CASCADE,
		SET_NULL,
		RESTRICT,
		NO_ACTION;
	}

	Type type();

	String name();

	// -- foreign
	String foreignKey() default "";

	String referenceTable() default "";

	String referenceColumn() default "";

	OnEvent onDelete() default OnEvent.NO_ACTION;

	OnEvent onUpdate() default OnEvent.NO_ACTION;

	// -- unique
	// -- primary key
	String[] columns() default {};

	// -- check
	String check() default "";
	
}
