
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.view.ViewStructure;

public class ViewStructureTest {

	@Test
	public void tableClassNameToTableNameHandlesEdgeCasesAndRoConventions() {
		assertNull(ViewStructure.viewClassNameToTableName((String) null));
		assertEquals("", ViewStructure.viewClassNameToTableName(""));
		assertEquals("person", ViewStructure.viewClassNameToTableName("PersonView"));
		assertEquals("ro_person", ViewStructure.viewClassNameToTableName("ROPersonView"));
		assertEquals("ro_person", ViewStructure.viewClassNameToTableName("PersonROView"));
		assertEquals("api_access_log", ViewStructure.viewClassNameToTableName("APIAccessLogView"));
	}

}
