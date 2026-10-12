package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import lu.kbra.pclib.db.exception.DBException;

public interface DatabaseMigrations {

	/**
	 * Stable identifier.
	 *
	 * NEVER change this after the migration has been released.
	 */
	String id();

	String name();

	/**
	 * Lower values run first.
	 */
	int order();

	default String description() {
		return this.name();
	}

	void up(Connection connection) throws DBException;

}
