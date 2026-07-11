package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DB_View {

	@QueryableHint(type = DefaultQueryableHints.VIEW_CONDITION)
	String condition() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_CUSTOM_SQL)
	String customSQL() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_GROUP_BY)
	String[] groupBy() default {};

	@QueryableHint(type = DefaultQueryableHints.NAME_OVERRIDE)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_ORDER_BY)
	OrderBy[] orderBy() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_TABLES)
	ViewTable[] tables();

	@QueryableHint(type = DefaultQueryableHints.VIEW_UNION_TABLES)
	UnionTable[] unionTables() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_WITH_TABLES)
	ViewWithTable[] with() default {};

	@DbmsFilter
	String dbms() default DataBaseEntryUtils.DBMS_FILTER_ALL;

}
