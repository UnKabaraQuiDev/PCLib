package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE })
public @interface GeneratedExpression {

	@ColumnHint(type = DefaultColumnHints.GENERATED_COLUMN)
	boolean value() default true;

}
