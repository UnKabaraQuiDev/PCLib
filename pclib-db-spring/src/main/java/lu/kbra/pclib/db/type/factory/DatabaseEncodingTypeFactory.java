package lu.kbra.pclib.db.type.factory;

import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

public interface DatabaseEncodingTypeFactory extends EncodingTypeRegistry {

	boolean matches(String protocol);

	default void tryAppendTypes(final DatabaseEntryUtils databaseEntryUtils) {
		if (this.matches(databaseEntryUtils.getDbmsQualifierName())) {
			databaseEntryUtils.appendEncodingTypes(this);
		}
	}

}
