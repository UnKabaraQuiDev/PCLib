import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.utils.SimpleTransformingQuery;

public class SimpleTransformingQueryTest {

	private static final class DummyEntry implements DataBaseEntry {
		private final String value;

		private DummyEntry(final String value) {
			this.value = value;
		}
	}

	@Test
	public void transformReturnsExpectedEntriesForHappyPaths() throws SQLException {
		final DummyEntry first = new DummyEntry("first");
		final DummyEntry second = new DummyEntry("second");
		final List<DummyEntry> data = Arrays.asList(first, second);

		assertSame(first, SimpleTransformingQuery.transform(data, Query.Type.FIRST_THROW));
		assertSame(first, SimpleTransformingQuery.transform(data, Query.Type.FIRST_NULL));
		assertSame(data, SimpleTransformingQuery.transform(data, Query.Type.LIST_EMPTY));
		assertSame(data, SimpleTransformingQuery.transform(data, Query.Type.LIST_THROW));
		assertSame(first, SimpleTransformingQuery.transform(Collections.singletonList(first), Query.Type.SINGLE_THROW));
		assertSame(first, SimpleTransformingQuery.transform(Collections.singletonList(first), Query.Type.SINGLE_NULL));
	}

	@Test
	public void transformReturnsNullWhenNullVariantsAllowMissingResults() throws SQLException {
		assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.FIRST_NULL));
		assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.SINGLE_NULL));
		assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.LIST_NULL));
	}

	@Test
	public void transformThrowsClearSqlExceptionsForInvalidCardinality() {
		final SQLException firstThrow = assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.FIRST_THROW));
		assertEquals("Expected at least one result, but got none.", firstThrow.getMessage());

		final SQLException singleThrow = assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Arrays.asList(new DummyEntry("a"), new DummyEntry("b")), Query.Type.SINGLE_THROW));
		assertEquals("Expected exactly one result, but got 2.", singleThrow.getMessage());

		final SQLException singleNull = assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Arrays.asList(new DummyEntry("a"), new DummyEntry("b")), Query.Type.SINGLE_NULL));
		assertEquals("Expected at most one result, but got 2.", singleNull.getMessage());

		final SQLException listThrow = assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.LIST_THROW));
		assertEquals("Expected a non-empty list, but got none.", listThrow.getMessage());
	}

}