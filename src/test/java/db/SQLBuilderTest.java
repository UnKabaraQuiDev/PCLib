package db;
import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.db.utils.BaseSQLEntryUtils;

public class SQLBuilderTest {
	
	@Test
	public void test() {
		new BaseSQLEntryUtils().getColumns(TestData.class);
	}
}
