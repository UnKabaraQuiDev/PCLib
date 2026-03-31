import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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

		Assertions.assertSame(first, SimpleTransformingQuery.transform(data, Query.Type.FIRST_THROW));
		Assertions.assertSame(first, SimpleTransformingQuery.transform(data, Query.Type.FIRST_NULL));
		Assertions.assertSame(data, SimpleTransformingQuery.transform(data, Query.Type.LIST_EMPTY));
		Assertions.assertSame(data, SimpleTransformingQuery.transform(data, Query.Type.LIST_THROW));
		Assertions.assertSame(first, SimpleTransformingQuery.transform(Collections.singletonList(first), Query.Type.SINGLE_THROW));
		Assertions.assertSame(first, SimpleTransformingQuery.transform(Collections.singletonList(first), Query.Type.SINGLE_NULL));
	}

	@Test
	public void transformReturnsNullWhenNullVariantsAllowMissingResults() throws SQLException {
		Assertions.assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.FIRST_NULL));
		Assertions.assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.SINGLE_NULL));
		Assertions.assertNull(SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.LIST_NULL));
	}

	@Test
	public void transformThrowsClearSqlExceptionsForInvalidCardinality() {
		final SQLException firstThrow = Assertions.assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.FIRST_THROW));
		Assertions.assertEquals("Expected at least one result, but got none.", firstThrow.getMessage());

		final SQLException singleThrow = Assertions.assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Arrays.asList(new DummyEntry("a"), new DummyEntry("b")), Query.Type.SINGLE_THROW));
		Assertions.assertEquals("Expected exactly one result, but got 2.", singleThrow.getMessage());

		final SQLException singleNull = Assertions.assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Arrays.asList(new DummyEntry("a"), new DummyEntry("b")), Query.Type.SINGLE_NULL));
		Assertions.assertEquals("Expected at most one result, but got 2.", singleNull.getMessage());

		final SQLException listThrow = Assertions.assertThrows(SQLException.class,
				() -> SimpleTransformingQuery.transform(Collections.<DummyEntry>emptyList(), Query.Type.LIST_THROW));
		Assertions.assertEquals("Expected a non-empty list, but got none.", listThrow.getMessage());
	}

}
