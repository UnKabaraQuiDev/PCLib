package lu.kbra.pclib.db.annotations.queryable;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@Repeatable(QueryableHints.class)
public @interface QueryableHint {

	@DbmsFilter
	String dbms() default "";

	String type();

	String value() default "";
	
	boolean grouped() default false;
	
	boolean repeatable() default false;

}
