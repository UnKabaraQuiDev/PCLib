package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD })
public @interface QueryFunction {

	@QueryHint(type = DefaultQueryHints.QUERY_METHOD_NAME)
	String methodName() default "*";

	@QueryHint(type = DefaultQueryHints.QUERY_FIELD_NAME)
	String fieldName() default "*";

	@DbmsFilter
	String dbms() default "";

}
