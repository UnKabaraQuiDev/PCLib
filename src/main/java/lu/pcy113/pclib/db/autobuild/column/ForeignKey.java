package lu.pcy113.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.autobuild.table.ForeignKeyData.OnAction;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ForeignKey {

	int groupId() default 0;
	
	Class<? extends DataBaseTable<?>> table();

	String column() default "";

	OnAction onDelete() default OnAction.NO_ACTION;

	OnAction onUpdate() default OnAction.NO_ACTION;

}
