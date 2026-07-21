package lu.kbra.pclib.db.annotations.entry.def;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.SizeClass;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target({ TYPE_USE })
public @interface Size {

	@TypeHint(type = DefaultTypeHints.SIZE_CLASS)
	SizeClass value();

	@DbmsFilter
	String dbms() default DatabaseEntryUtils.DBMS_FILTER_ALL;

}
