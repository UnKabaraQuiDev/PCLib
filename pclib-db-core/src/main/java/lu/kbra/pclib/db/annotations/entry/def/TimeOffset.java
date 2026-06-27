package lu.kbra.pclib.db.annotations.entry.def;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, TYPE_USE })
public @interface TimeOffset {

	@TypeHint(type = DefaultTypeHints.OFFSET_ID)
	String value();

}
