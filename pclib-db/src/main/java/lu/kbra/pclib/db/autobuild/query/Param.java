package lu.kbra.pclib.db.autobuild.query;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param {

	String value() default "";

	boolean ignoreNull() default false;

	String comparator() default "=";

}
