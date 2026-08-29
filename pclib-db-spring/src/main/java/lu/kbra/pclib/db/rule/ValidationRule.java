package lu.kbra.pclib.db.rule;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.InsertRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.UpdateRule;
import lu.kbra.pclib.db.validation.TableValidatorFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidationRule implements PrepareRule, InsertRule, UpdateRule {

	public static final String SKIP_VALIDATION = "SKIP_VALIDATION";

	private final Map<TableStructure, Validator> validatorCache = new HashMap<>();

	private final TableValidatorFactory tableValidatorFactory;

	@Override
	public void executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection c, final Object data) {
		if (queryable.getStructure().getBooleanHint(SKIP_VALIDATION)) {
			return;
		}

		final Validator validator = validatorCache.computeIfAbsent((TableStructure) queryable.getStructure(),
				tableValidatorFactory::createValidator);

		if (data instanceof Collection<?> col) {
			col.forEach((final Object entry) -> validate(validator, entry));
		} else {
			validate(validator, data);
		}
	}

	private void validate(final Validator validator, final Object data) {
		final Set<ConstraintViolation<Object>> violations = validator.validate(data);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return hookType.isPrepare() && (hookType.isInsert() || hookType.isUpdate());
	}

}
