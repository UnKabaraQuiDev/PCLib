package lu.pcy113.pclib.db.annotations.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DB_Base {

	String name();
	
	String characterSet() default "utf8mb4";
	
	String collate() default "utf8mb4_general_ci";

}
