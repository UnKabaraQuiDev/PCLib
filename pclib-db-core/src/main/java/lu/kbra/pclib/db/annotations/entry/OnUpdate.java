package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@GeneratedExpression
public @interface OnUpdate {

	String TABLE_NAME_KEY = DatabaseEntryUtils.TABLE_NAME_KEY;
	String TABLE_NAME = "{" + OnUpdate.TABLE_NAME_KEY + "}";
	String FIELD_NAME_KEY = DatabaseEntryUtils.FIELD_NAME_KEY;
	String FIELD_NAME = "{" + OnUpdate.FIELD_NAME_KEY + "}";
	String QUALIFIER_KEY = DatabaseEntryUtils.QUALIFIER_KEY;
	String FUNCTION_KEY = DatabaseEntryUtils.FUNCTION_KEY;
	String MEMBER_KEY = DatabaseEntryUtils.MEMBER_KEY;

	@ColumnHint(type = DefaultColumnHints.ON_UPDATE)
	String value();

	@DbmsFilter
	String dbms() default "";

}
