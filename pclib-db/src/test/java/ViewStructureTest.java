
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.view.ViewStructure;

public class ViewStructureTest {

	@Test
	public void tableClassNameToTableNameHandlesEdgeCasesAndRoConventions() {
		Assert.assertNull(ViewStructure.viewClassNameToTableName((String) null));
		Assertions.assertEquals("", ViewStructure.viewClassNameToTableName(""));
		Assertions.assertEquals("person", ViewStructure.viewClassNameToTableName("PersonView"));
		Assertions.assertEquals("ro_person", ViewStructure.viewClassNameToTableName("ROPersonView"));
		Assertions.assertEquals("ro_person", ViewStructure.viewClassNameToTableName("PersonROView"));
		Assertions.assertEquals("api_access_log", ViewStructure.viewClassNameToTableName("APIAccessLogView"));
	}

}
