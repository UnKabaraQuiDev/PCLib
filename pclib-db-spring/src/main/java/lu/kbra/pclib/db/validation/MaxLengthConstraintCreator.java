package lu.kbra.pclib.db.validation;

import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.defs.SizeDef;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

@Component
public class MaxLengthConstraintCreator implements ConstraintCreator {

	@Override
	public boolean createConstraint(final ColumnData column, final PropertyConstraintMappingContext fieldMapping) {
		Object maxLength = column.getTypeHint(DefaultTypeHints.MAX_LENGTH);

		if (maxLength == null) {
			maxLength = column.getTypeHint(DefaultTypeHints.FIXED_LENGTH);
		}

		if (maxLength == null) {
			return false;
		}

		fieldMapping.constraint(new SizeDef().max((Integer) maxLength));

		return true;
	}

}
