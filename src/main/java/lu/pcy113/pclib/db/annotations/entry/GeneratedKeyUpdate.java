package lu.pcy113.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface GeneratedKeyUpdate {
	
	Type type() default Type.RESULT_SET; // for backwards compatibility
	
	int index() default 1;
	
	enum Type {
		INDEX,
		RESULT_SET
	}
	

}
