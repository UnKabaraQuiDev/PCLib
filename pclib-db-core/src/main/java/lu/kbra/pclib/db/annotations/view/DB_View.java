package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.autobuild.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.autobuild.table.meta.TableHint;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DB_View {

	String condition() default "";

	String customSQL() default "";

	String[] groupBy() default {};

	@TableHint(type = DefaultTableHints.NAME_OVERRIDE)
	String name() default "";

	OrderBy[] orderBy() default {};

	ViewTable[] tables();

	UnionTable[] unionTables() default {};

	ViewWithTable[] with() default {};

}
