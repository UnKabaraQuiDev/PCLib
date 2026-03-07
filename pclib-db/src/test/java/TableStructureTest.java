
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class TableStructureTest {

	@Test
	public void classNameToTableNameHandlesEdgeCasesAndRoConventions() {
		assertNull(TableStructure.classNameToTableName(null));
		assertEquals("", TableStructure.classNameToTableName(""));
		assertEquals("person", TableStructure.classNameToTableName("PersonData"));
		assertEquals("ro_person", TableStructure.classNameToTableName("ROPersonData"));
		assertEquals("ro_person", TableStructure.classNameToTableName("PersonROData"));
		assertEquals("api_access_log", TableStructure.classNameToTableName("APIAccessLogData"));
	}

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

	@Test
	public void buildIncludesColumnsConstraintsAndMysqlOptions() {
		final ColumnData id = new ColumnData("id", new IntTypes.IntType(), true, false, null, null);
		final ConstraintData pk = new ConstraintData() {
			@Override
			public String getName() {
				return "pk_people";
			}

			@Override
			public String build(final DataBaseConnector connector) {
				return "CONSTRAINT `pk_people` PRIMARY KEY (`id`)";
			}
		};

		final TableStructure structure = new TableStructure("people", new ColumnData[] { id }, new ConstraintData[] { pk });
		structure.setCharacterSet("utf8mb4");
		structure.setEngine("InnoDB");

		final String sql = structure.build(new MySQLDataBaseConnector("user", "pass", "localhost", 3306));

		assertTrue(sql.startsWith("CREATE TABLE `people` (\n"));
		assertTrue(sql.contains("  `id` INT AUTO_INCREMENT NOT NULL"));
		assertTrue(sql.contains("  CONSTRAINT `pk_people` PRIMARY KEY (`id`)"));
		assertTrue(sql.endsWith(") CHARACTER SET utf8mb4 ENGINE=InnoDB;\n"));
	}

}
