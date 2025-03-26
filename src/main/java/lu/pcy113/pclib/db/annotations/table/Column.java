package lu.pcy113.pclib.db.annotations.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Column {

	public enum GeneratedType {
		STORED, VIRTUAL;
	}

	String name();

	String type();

	boolean autoIncrement() default false;

	boolean notNull() default true;

	boolean generated() default false;

	String generator() default "";

	GeneratedType generatedType() default GeneratedType.VIRTUAL;

	String default_() default "";

	String onUpdate() default "";

}
