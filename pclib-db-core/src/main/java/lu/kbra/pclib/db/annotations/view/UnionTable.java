package lu.kbra.pclib.db.annotations.view;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.SQLQueryable;

@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
//@QueryableHint(type = DefaultQueryableHints.VIEW_UNION_TABLE, repeatable = true, grouped = true)
public @interface UnionTable {

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMNS)
	ViewColumn[] columns() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_CONDITION)
	String condition() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_NAME)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_TYPE)
	Class<? extends SQLQueryable<?>> typeName();

}
