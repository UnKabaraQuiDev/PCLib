package lu.pcy113.pclib.db.annotations.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface DB_Table {

	String name();
	
	Column[] columns();
	
	Constraint[] constraints() default {};
	
	String characterSet() default "";
	
	String collation() default "";
	
	String engine() default "";
	
}
