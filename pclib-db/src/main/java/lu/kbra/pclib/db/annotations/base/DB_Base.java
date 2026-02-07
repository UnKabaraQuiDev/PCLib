package lu.kbra.pclib.db.annotations.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
/**
 * Initialize the name in the constructor instead.
 * 
 * @see {@link lu.kbra.pclib.db.base.DataBase DataBase}
 */
public @interface DB_Base {

	String name();

	String characterSet() default MySQLDataBaseConnector.DEFAULT_CHARSET;

	String collate() default MySQLDataBaseConnector.DEFAULT_COLLATION;

}
