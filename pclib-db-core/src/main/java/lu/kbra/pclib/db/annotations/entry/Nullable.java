package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.query.QueryHint;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
public @interface Nullable {

	/**
	 * Indicates whether the column is nullable.<br>
	 * If true, the column can contain null values.<br>
	 * If false, the column must have a value.
	 */
	@ColumnHint(type = DefaultColumnHints.NULLABLE)
	@QueryHint(type = DefaultQueryHints.PARAM_NULLABLE)
	boolean value() default true;

}
