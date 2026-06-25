package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, TYPE })
@Repeatable(Checks.class)
public @interface Check {

	String FIELD_NAME_PLACEHOLDER = "{FIELD}";
	String TABLE_NAME_PLACEHOLDER = "{TABLE}";

	String name() default "";

	String value();

}
