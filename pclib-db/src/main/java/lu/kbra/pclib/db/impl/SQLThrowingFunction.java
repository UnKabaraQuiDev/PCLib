package lu.kbra.pclib.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLThrowingFunction<T> {

	T apply(ResultSet t) throws SQLException;

}
