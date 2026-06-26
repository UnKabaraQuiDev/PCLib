package lu.kbra.pclib.db.autobuild.table.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@Repeatable(QueryableHints.class)
public @interface QueryableHint {

	String type();

	String value() default "";

	String dbms() default "";

}
