package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewTable {

	public static enum Type {
		MAIN, MAIN_UNION, MAIN_UNION_ALL, LEFT, RIGHT, INNER, FULL, CROSS;
	}

	String name() default "";

	Class<?> typeName() default Class.class;

	String asName() default "";

	Type join() default Type.MAIN;

	boolean distinct() default false;

	ViewColumn[] columns() default {};

	String on() default "";

}
