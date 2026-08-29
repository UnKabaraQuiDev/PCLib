package lu.kbra.pclib.db.validation;

import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.defs.NotNullDef;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;

@Component
public class NullableConstraintCreator implements ConstraintCreator {

	@Override
	public boolean createConstraint(ColumnData column, PropertyConstraintMappingContext fieldMapping) {
		final boolean nullable = column.getBooleanHint(DefaultColumnHints.NULLABLE) || column.hasDefaultValue() || column.isAutoIncrement();

		if (nullable) {
			return false;
		}

		fieldMapping.constraint(new NotNullDef());

		return true;
	}

}
