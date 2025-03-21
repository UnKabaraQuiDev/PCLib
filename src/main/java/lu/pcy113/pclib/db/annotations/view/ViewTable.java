package lu.pcy113.pclib.db.annotations.view;

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
		MAIN, LEFT, RIGHT, INNER, FULL, CROSS;
	}
	
	String name();
	
	Type join() default Type.MAIN;
	
	ViewColumn[] columns() default {};
	
	String on() default "";
	
}
