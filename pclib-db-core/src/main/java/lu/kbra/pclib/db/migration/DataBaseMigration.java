package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;

public interface DataBaseMigration {

	/**
	 * Lower values run first.
	 */
	int order();

	String name();

	void up(DataBase dataBase, Connection connection) throws DBException;

	default void down(final DataBase dataBase, final Connection connection) throws DBException {
		throw new UnsupportedOperationException("Down migration is not implemented for " + this.id() + ".");
	}

	default boolean shouldRun(final DataBase dataBase) {
		return true;
	}

	default String id() {
		return String.format("%06d_%s", this.order(), this.name());
	}

}
