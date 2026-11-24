package lu.kbra.pclib.db.type.factory;

import lu.kbra.pclib.db.dbms.registry.EncodingTypeRegistry;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public interface DatabaseEncodingTypeFactory extends EncodingTypeRegistry {

	/**
	 * @deprecated Implement {@link #supports} instead
	 */
	@Deprecated
	default boolean matches(final String protocol) {
		return this.supports(protocol);
	}

	default void tryAppendTypes(final DatabaseEntryUtils databaseEntryUtils) {
		if (this.supports(databaseEntryUtils.getDbmsQualifierName()) && this.shouldRegister()) {
			databaseEntryUtils.appendEncodingTypes(this);
		}
	}

}
