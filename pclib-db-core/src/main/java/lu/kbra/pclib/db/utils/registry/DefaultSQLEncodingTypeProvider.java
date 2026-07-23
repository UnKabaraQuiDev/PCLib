package lu.kbra.pclib.db.utils.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.NoMatchingTypeFoundException;
import lu.kbra.pclib.db.exception.TypeClassNotFoundException;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;

import lombok.Getter;

@Getter
public class DefaultSQLEncodingTypeProvider implements SQLEncodingTypeProvider {

	protected final List<EncodingTypeFactory<?, ?>> EncodingTypeFactories;

	public DefaultSQLEncodingTypeProvider() {
		this.EncodingTypeFactories = new ArrayList<>();
	}

	public DefaultSQLEncodingTypeProvider(final List<EncodingTypeFactory<?, ?>> EncodingTypeFactories) {
		this.EncodingTypeFactories = EncodingTypeFactories;
	}

	@Override
	public <T> EncodingType<T> getTypeFor(final Class<T> storedType, final HintsOwner typeHints) {
		return this.computeType(storedType, typeHints)
				.findFirst()
				.orElseThrow(() -> new NoMatchingTypeFoundException("No suitable encoding type found for: " + storedType.getName()
						+ (DBException.INCLUDE_TYPE_HINTS_IN_EXCEPTION ? "\n --- Type hints ---" + PCUtils.printTree(typeHints.getHints())
								: "")))
				.get(typeHints);
	}

	@Override
	public <T> Stream<EncodingTypeFactory<?, T>> computeType(final Class<T> storedType, final HintsOwner typeHints) {
		Objects.requireNonNull(storedType, "storedType is null.");
		Objects.requireNonNull(typeHints, "typeHints is null.");

		final Class<?> clazz;
		if (typeHints.hasHint(DefaultTypeHints.TYPE_OVERRIDE)) {
			try {
				final Object typeOverride = typeHints.getHint(DefaultTypeHints.TYPE_OVERRIDE);
				clazz = typeOverride instanceof Class ? (Class<?>) typeOverride : Class.forName(Objects.toString(typeOverride));
			} catch (final ClassNotFoundException e) {
				throw new TypeClassNotFoundException(e);
			}
		} else {
			clazz = storedType;
		}

		return this.EncodingTypeFactories.stream()
				.map(entry -> new Pair<>(entry.eval(clazz, typeHints), entry))
				.filter(entry -> !Objects.equals(entry.getKey(), EncodingTypeRegistry.EXCLUDE))
				.sorted(Comparator.comparingInt(e -> -e.getKey()))
				.map(v -> (EncodingTypeFactory<?, T>) v.getValue());
	}

}
