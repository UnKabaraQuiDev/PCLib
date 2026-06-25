package lu.kbra.pclib.db.autobuild.column.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, METHOD, TYPE_USE })
public @interface DecimalParam {

	@TypeHint(type = DefaultTypeHints.PRECISION)
	int precision();

	@TypeHint(type = DefaultTypeHints.SCALE)
	int scale();

}
