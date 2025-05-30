package lu.pcy113.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface Column {

	String name() default "";

	Class<?> type() default Class.class;

	int length() default -1;

	String[] params() default {};

}
