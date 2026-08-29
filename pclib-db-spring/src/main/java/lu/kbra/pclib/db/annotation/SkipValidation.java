package lu.kbra.pclib.db.annotation;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.rule.ValidationRule;

@Documented
@Retention(RUNTIME)
@Target(TYPE_USE)
public @interface SkipValidation {

	@QueryableHint(type = ValidationRule.SKIP_VALIDATION)
	boolean value() default true;

}
