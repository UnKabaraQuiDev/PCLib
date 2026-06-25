package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.autobuild.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.SQLQueryable;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ForeignKey {

	String column() default "";

	int groupId() default 0;

	OnAction onDelete() default OnAction.NO_ACTION;

	OnAction onUpdate() default OnAction.NO_ACTION;

	Class<? extends SQLQueryable<?>> table();

}
