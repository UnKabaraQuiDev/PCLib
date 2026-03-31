
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class TableStructureTest {

	@Test
	public void entryClassNameToTableNameHandlesEdgeCasesAndRoConventions() {
		Assert.assertNull(TableStructure.entryClassNameToTableName(null));
		Assertions.assertEquals("", TableStructure.entryClassNameToTableName(""));
		Assertions.assertEquals("person", TableStructure.entryClassNameToTableName("PersonData"));
		Assertions.assertEquals("ro_person", TableStructure.entryClassNameToTableName("ROPersonData"));
		Assertions.assertEquals("ro_person", TableStructure.entryClassNameToTableName("PersonROData"));
		Assertions.assertEquals("api_access_log", TableStructure.entryClassNameToTableName("APIAccessLogData"));
	}

	@Test
	public void tableClassNameToTableNameHandlesEdgeCasesAndRoConventions() {
		Assert.assertNull(TableStructure.tableClassNameToTableName((String) null));
		Assertions.assertEquals("", TableStructure.tableClassNameToTableName(""));
		Assertions.assertEquals("person", TableStructure.tableClassNameToTableName("PersonTable"));
		Assertions.assertEquals("ro_person", TableStructure.tableClassNameToTableName("ROPersonTable"));
		Assertions.assertEquals("ro_person", TableStructure.tableClassNameToTableName("PersonROTable"));
		Assertions.assertEquals("api_access_log", TableStructure.tableClassNameToTableName("APIAccessLogTable"));
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

		Assertions.assertTrue(sql.startsWith("CREATE TABLE `people` (\n"));
		Assertions.assertTrue(sql.contains("  `id` INT AUTO_INCREMENT NOT NULL"));
		Assertions.assertTrue(sql.contains("  CONSTRAINT `pk_people` PRIMARY KEY (`id`)"));
		Assertions.assertTrue(sql.endsWith(") CHARACTER SET utf8mb4 ENGINE=InnoDB;\n"));
	}

}
