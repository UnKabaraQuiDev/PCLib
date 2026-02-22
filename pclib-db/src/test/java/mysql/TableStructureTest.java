package mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.table.TableStructure;

public class TableStructureTest {

	@Test
	public void test() {
		assertEquals("person", TableStructure.classNameToTableName("PersonData"));
		assertEquals("ro_person", TableStructure.classNameToTableName("ROPersonData"));
		assertEquals("ro_person", TableStructure.classNameToTableName("PersonROData"));
	}
	
}
