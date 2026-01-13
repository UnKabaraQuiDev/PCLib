package lu.pcy113.pclib.db.annotations.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.pcy113.pclib.db.DataBase;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DB_Base {

	String name();

	String characterSet() default DataBase.DEFAULT_CHARSET;

	String collate() default DataBase.DEFAULT_COLLATION;

}
