package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, TYPE })
@Repeatable(Checks.class)
@ColumnHint(type = DefaultColumnHints.CHECK, repeatable = true, grouped = true)
public @interface Check {

	String TABLE_NAME_KEY = DataBaseEntryUtils.TABLE_NAME_KEY;
	String TABLE_NAME = "{" + Check.TABLE_NAME_KEY + "}";
	String FIELD_NAME_KEY = DataBaseEntryUtils.FIELD_NAME_KEY;
	String FIELD_NAME = "{" + Check.FIELD_NAME_KEY + "}";
	String QUALIFIER_KEY = DataBaseEntryUtils.QUALIFIER_KEY;
	String FUNCTION_KEY = DataBaseEntryUtils.FUNCTION_KEY;
	String MEMBER_KEY = DataBaseEntryUtils.MEMBER_KEY;

	@ColumnHint(type = DefaultColumnHints.CHECK_NAME)
	String name() default "";

	@ColumnHint(type = DefaultColumnHints.CHECK_VALUE)
	String value();

	@DbmsFilter
	String dbms() default "";

}
