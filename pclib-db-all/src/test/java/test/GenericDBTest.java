package test;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;

public interface GenericDBTest {

	Database getDatabase();

	DatabaseConnector getConnector();

	DatabaseConnector createConnector() throws Exception;

	static <T extends Throwable> T assertThrowsInCauses(final Class<T> expectedType, final ThrowingRunnable executable) {
		final Throwable thrown = Assert.assertThrows(Throwable.class, executable);

		for (Throwable cause = thrown; cause != null; cause = cause.getCause()) {
			if (expectedType.isInstance(cause)) {
				return expectedType.cast(cause);
			}
		}

		Assert.fail("Expected " + expectedType.getName() + " in the cause chain, but got " + PCUtils.toString(thrown));
		return null; // unreachable
	}

}
