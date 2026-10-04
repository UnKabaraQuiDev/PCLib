package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.DBException;

public interface DatabaseMigration {

	default String id() {
		return String.format("%06d_%s", this.order(), this.name());
	}

	String name();

	/**
	 * Lower values run first.
	 */
	int order();

	default boolean shouldRun(final Database database) {
		return true;
	}

	void up(Connection connection) throws DBException;

}
