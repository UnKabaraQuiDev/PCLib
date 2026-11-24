package lu.kbra.pclib.db.type.factory;

import lu.kbra.pclib.db.dbms.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public interface DatabaseColumnTypeFactory extends ColumnTypeRegistry {

	/**
	 * @deprecated Implement {@link #supports} instead
	 */
	@Deprecated
	default boolean matches(final String protocol) {
		return this.supports(protocol);
	}

	@Override
	default boolean supports(String dbmsQualifierName) {
		throw new UnsupportedOperationException("Unimplemented.");
	}

	default void tryAppendTypes(final DatabaseEntryUtils databaseEntryUtils) {
		if (this.supports(databaseEntryUtils.getDbmsQualifierName()) && this.shouldRegister()) {
			databaseEntryUtils.appendColumnTypes(this);
		}
	}

}
