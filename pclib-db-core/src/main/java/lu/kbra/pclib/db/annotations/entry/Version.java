package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE })
@ExperimentalApi
@ColumnHint(type = DefaultColumnHints.VERSION, value = "true")
public @interface Version {

	String FIELD_NAME = "{" + DatabaseEntryUtils.FIELD_NAME_KEY + "}";

	@DbmsFilter
	String dbms() default DatabaseEntryUtils.DBMS_FILTER_ALL;

	@ColumnHint(type = DefaultColumnHints.UPDATE_EXPR)
	String expr() default Version.FIELD_NAME + " + 1";

	@ColumnHint(type = DefaultColumnHints.DEFAULT_VALUE)
	String default_() default "0";

	@ColumnHint(type = DefaultColumnHints.UPDATE_EXPR_VALUE)
	boolean current() default false;

}
