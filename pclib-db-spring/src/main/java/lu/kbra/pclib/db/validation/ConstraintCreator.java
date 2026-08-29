package lu.kbra.pclib.db.validation;

import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;

import lu.kbra.pclib.db.domain.column.ColumnData;

public interface ConstraintCreator {

	/**
	 * @return true if a constraint was added
	 */
	boolean createConstraint(ColumnData column, PropertyConstraintMappingContext fieldMapping);

}
