package lu.kbra.pclib.db.validation;

import java.util.List;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.utils.FieldStorageBinding;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TableValidatorFactory {

	private final List<ConstraintCreator> constraintCreators;

	public Validator createValidator(final TableStructure structure) {
		final HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();
		final ConstraintMapping mapping = configuration.createConstraintMapping();
		final TypeConstraintMappingContext<? extends DatabaseEntry> typeMapping = mapping.type(structure.getEntryClass());

		for (final ColumnData column : structure.getColumns()) {
			if (!(column.getStorageBinding() instanceof final FieldStorageBinding binding)) {
				return null;
			}

			final String fieldName = binding.getField().getName();
			final PropertyConstraintMappingContext fieldMapping = typeMapping.field(fieldName);

			for (final ConstraintCreator creator : constraintCreators) {
				creator.createConstraint(column, fieldMapping);
			}
		}

		return configuration.addMapping(mapping).buildValidatorFactory().getValidator();
	}
}
