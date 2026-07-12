package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.DBException;

@ExperimentalApi
public interface DatabaseMigration {

	default void down(final Database database, final Connection connection) throws DBException {
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

	default boolean shouldRun(final Database database) {
		return true;
	}

	void up(Database database, Connection connection) throws DBException;

}
