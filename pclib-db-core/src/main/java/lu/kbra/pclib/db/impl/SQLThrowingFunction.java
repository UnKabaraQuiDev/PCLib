package lu.kbra.pclib.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.impl.function.ThrowingFunction;

@FunctionalInterface
public interface SQLThrowingFunction<T> extends ThrowingFunction<ResultSet, T, SQLException> {

}
