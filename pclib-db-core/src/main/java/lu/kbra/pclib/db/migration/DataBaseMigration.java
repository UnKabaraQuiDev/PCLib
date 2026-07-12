package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;

@ExperimentalApi
public interface DataBaseMigration {

	default void down(final DataBase dataBase, final Connection connection) throws DBException {
		throw new UnsupportedOperationException("Down migration is not implemented for " + this.id() + ".");
	}

	default String id() {
		return String.format("%06d_%s", this.order(), this.name());
	}

	String name();

	/**
	 * Lower values run first.
	 */
	int order();

	default boolean shouldRun(final DataBase dataBase) {
		return true;
	}

	void up(DataBase dataBase, Connection connection) throws DBException;

}
