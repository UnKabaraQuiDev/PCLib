package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.NoMatchingTypeFoundException;
import lu.kbra.pclib.db.exception.TypeClassNotFoundException;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;

import lombok.Getter;

@Getter
public class DefaultSQLColumnTypeProvider implements SQLColumnTypeProvider {

	protected final List<ColumnTypeFactory> columnTypeFactories;

	public DefaultSQLColumnTypeProvider() {
		this.columnTypeFactories = new ArrayList<>();
	}

	public DefaultSQLColumnTypeProvider(final List<ColumnTypeFactory> columnTypeFactories) {
		this.columnTypeFactories = columnTypeFactories;
	}

	@Override
	public ColumnType getTypeFor(final Class<?> clazz, final Optional<AnnotatedType> type, final Map<String, Object> typeHints) {
		return this.computeType(clazz, typeHints)
				.findFirst()
				.orElseThrow(() -> new NoMatchingTypeFoundException("No suitable type found: " + clazz.getName()
						+ (DBException.INCLUDE_TYPE_HINTS_IN_EXCEPTION ? "\n --- Type hints ---" + PCUtils.printTree(typeHints) : "")))
				.get(type, typeHints);
	}

	@Override
	public Stream<ColumnTypeFactory> computeType(final Class<?> rawType, final Map<String, Object> typeHints) {
		Objects.requireNonNull(rawType, "rawType is null.");
		Objects.requireNonNull(typeHints, "typeHints is null.");

		final Class<?> clazz;
		if (typeHints.containsKey(DefaultTypeHints.TYPE_OVERRIDE)) {
			try {
				final Object typeOverride = typeHints.get(DefaultTypeHints.TYPE_OVERRIDE);
				clazz = typeOverride instanceof Class ? (Class<?>) typeOverride : Class.forName(Objects.toString(typeOverride));
			} catch (final ClassNotFoundException e) {
				throw new TypeClassNotFoundException(e);
			}
		} else {
			clazz = rawType;
		}

		return this.columnTypeFactories.stream()
				.map(entry -> new Pair<>(entry.eval(clazz, typeHints), entry))
				.filter(entry -> entry.getKey() != ColumnTypeRegistry.EXCLUDE)
				.sorted(Comparator.comparingInt(e -> -e.getKey()))
				.map(Pair::getValue);
	}

}
