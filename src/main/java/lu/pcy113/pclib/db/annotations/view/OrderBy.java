package lu.pcy113.pclib.db.annotations.view;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface OrderBy {

	public static enum Type {
		ASC, DESC;
	}
	
	String column();
	
	Type type() default Type.ASC;
	
}
