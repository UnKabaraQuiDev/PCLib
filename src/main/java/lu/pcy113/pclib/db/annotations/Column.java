package lu.pcy113.pclib.db.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Column {

	String name();
	
	String type();

	boolean autoIncrement() default false;
	
	boolean primaryKey() default false;
	
	boolean notNull() default true;
	
	boolean unique() default false;
	
	boolean index() default false;

	String default_() default "";
	
}
