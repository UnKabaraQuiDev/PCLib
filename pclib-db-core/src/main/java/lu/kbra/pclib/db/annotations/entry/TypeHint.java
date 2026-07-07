package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({  TYPE_USE, METHOD })
@Repeatable(TypeHints.class)
public @interface TypeHint {

	@DbmsFilter
	String dbms() default "";

	String type();

	String value() default "";

}
