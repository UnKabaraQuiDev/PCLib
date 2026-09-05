package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.table.DefaultQueryHints;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
@QueryHint(type = DefaultQueryHints.PARAM_PARAM, value = "true")
public @interface Param {

	@QueryHint(type = DefaultQueryHints.PARAM_COMPARATOR)
	String comparator() default "=";

	@QueryHint(type = DefaultQueryHints.PARAM_IGNORE_NULL)
	boolean ignoreNull() default false;

	@QueryHint(type = DefaultQueryHints.PARAM_NAME)
	String value() default "";

	@QueryHint(type = DefaultQueryHints.PARAM_MEMBER_NAME)
	String member() default "";

}
