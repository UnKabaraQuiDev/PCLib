package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@QueryableHint(type = DefaultQueryableHints.VIEW_WITH_TABLE, repeatable = true, grouped = true)
public @interface ViewWithTable {

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMNS)
	ViewColumn[] columns() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_CONDITION)
	String condition() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_GROUP_BY)
	String[] groupBy() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_AS_NAME)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_ORDER_BY)
	OrderBy[] orderBy() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_TABLES)
	ViewTable[] tables();

}
