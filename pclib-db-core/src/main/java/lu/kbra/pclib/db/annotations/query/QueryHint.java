package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, FIELD, METHOD, ANNOTATION_TYPE })
@Repeatable(QueryHints.class)
public @interface QueryHint {

	@DbmsFilter
	String dbms() default DatabaseEntryUtils.DBMS_FILTER_ALL;

	String type();

	String value() default "";

	boolean grouped() default false;

	boolean repeatable() default false;

}
