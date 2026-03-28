import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;

public class MySQLDataBaseConnectorTest {

	@Test
	public void updateCopiesConnectorDefaultsOnlyWhenValuesAreMissing() {
		final TableStructure structure = new TableStructure("people", new ColumnData[0], new ConstraintData[0]);
		final MySQLDataBaseConnector connector = new MySQLDataBaseConnector("user", "pass", "localhost", 3306);
		connector.setCharacterSet("utf8mb4");
		connector.setCollation("utf8mb4_0900_ai_ci");
		connector.setEngine("InnoDB");

		structure.update(connector);

		assertEquals("utf8mb4", structure.getCharacterSet());
		assertEquals("utf8mb4_0900_ai_ci", structure.getCollation());
		assertEquals("InnoDB", structure.getEngine());

		structure.setCharacterSet("latin1");
		structure.setCollation("latin1_swedish_ci");
		structure.setEngine("MyISAM");
		connector.setCharacterSet("utf16");
		connector.setCollation("utf16_general_ci");
		connector.setEngine("MEMORY");

		structure.update(connector);

		assertEquals("latin1", structure.getCharacterSet());
		assertEquals("latin1_swedish_ci", structure.getCollation());
		assertEquals("MyISAM", structure.getEngine());
	}

}
