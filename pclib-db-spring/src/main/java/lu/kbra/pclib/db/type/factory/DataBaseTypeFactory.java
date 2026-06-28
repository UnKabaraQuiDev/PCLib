package lu.kbra.pclib.db.type.factory;

import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DataBaseTypeFactory extends ColumnTypeRegistry {

	boolean matches(String protocol);

	default void tryAppendTypes(final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		if (this.matches(dataBaseEntryUtils.getDbmsQualifierName())) {
			dataBaseEntryUtils.appendTypes(this);
		}
	}

}
