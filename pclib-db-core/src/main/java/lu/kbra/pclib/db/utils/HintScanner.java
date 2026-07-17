package lu.kbra.pclib.db.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.ColumnHint;
import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.exception.DBException;

@Getter
@RequiredArgsConstructor
public class HintScanner {

	protected final String dbmsQualifierName;

	@Data
	@AllArgsConstructor
	private static class HintValue {
		String qualifier;
		Object value;
		boolean repeatable;
		String group;
	}

	@FunctionalInterface
	protected interface HintExtractor<A extends Annotation> {
		void extract(A annotation, Object value, Map<String, List<HintValue>> out, String qualifier, boolean repeatable, String group);
	}

	protected boolean matchesDbmsQualifier(final String dbms) {
		final String trimmed = dbms.trim();
		if (trimmed.isEmpty() || this.dbmsQualifierName.trim().isEmpty()) {
			return true;
		}
		return this.dbmsQualifierName.matches(PCUtils.globToRegex(trimmed));
	}

	protected boolean isEmptyValue(final Object value) {
		return value == null || value instanceof String && ((String) value).trim().isEmpty()
				|| value.getClass().isArray() && Array.getLength(value) == 0;
	}

	protected Map<String, Object> buildHints(final Map<String, List<HintValue>> collected) {
		final Map<String, Object> result = new LinkedHashMap<>();

		final Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
		final Map<String, List<Map<String, Object>>> groupedRepeatable = new LinkedHashMap<>();

		for (final Map.Entry<String, List<HintValue>> e : collected.entrySet()) {
			final List<HintValue> matching = e.getValue()
					.stream()
					.filter(v -> this.matchesDbmsQualifier(v.qualifier))
					.collect(Collectors.toList());

			if (matching.isEmpty()) {
				continue;
			}

			final boolean repeatable = matching.get(0).repeatable;
			final boolean groupedHint = matching.get(0).group != null;

			if (!groupedHint) {
				if (repeatable) {
					result.put(e.getKey(), matching.stream().map(v -> v.value).collect(Collectors.toList()));
					continue;
				}

				HintValue best = null;
				for (final HintValue value : matching) {
					if (best == null || best.qualifier.isEmpty() && !value.qualifier.isEmpty()) {
						best = value;
					}
				}

				int count = 0;
				for (final HintValue value : matching) {
					if (Objects.equals(best.qualifier, value.qualifier)) {
						count++;
					}
				}

				if (count > 1) {
					throw new IllegalArgumentException("Multiple values found for non-repeatable hint '" + e.getKey() + "'");
				}

				result.put(e.getKey(), best.value);
				continue;
			}

			if (!repeatable) {
				for (final HintValue value : matching) {
					grouped.computeIfAbsent(value.group, k -> new LinkedHashMap<>()).put(e.getKey(), value.value);
				}
			} else {
				for (final HintValue value : matching) {
					final List<Map<String, Object>> list = groupedRepeatable.computeIfAbsent(value.group, k -> new ArrayList<>());
					Map<String, Object> map;
					if (list.isEmpty()) {
						map = new LinkedHashMap<>();
						list.add(map);
					} else {
						map = list.get(0);
					}
					map.put(e.getKey(), value.value);
				}
			}
		}

		if (!grouped.isEmpty()) {
			result.putAll(grouped);
		}
		if (!groupedRepeatable.isEmpty()) {
			result.putAll(groupedRepeatable);
		}

		return result;
	}

	protected <A extends Annotation> Map<String, Object> computeAnnotationHints(
			final Annotation annotation,
			final Set<Class<?>> visited,
			final Class<A> hintType,
			final HintExtractor<A> extractor,
			final Predicate<A> repeatablePredicate,
			final Predicate<A> groupedPredicate,
			final Function<A, String> groupExtractor,
			final Function<A, String> dbmsExtractor) {

		try {
			final Map<String, List<HintValue>> collected = new LinkedHashMap<>();
			this.collect(annotation,
					collected,
					new HashSet<>(visited),
					hintType,
					extractor,
					repeatablePredicate,
					groupedPredicate,
					groupExtractor,
					dbmsExtractor,
					null);
			return this.buildHints(collected);
		} catch (final Exception e) {
			throw new DBException("Unable to compute nested hints for: " + annotation, e);
		}
	}

	protected <A extends Annotation> Map<String, Object> computeAnnotationHints(
			final Annotation annotation,
			final Class<A> hintType,
			final HintExtractor<A> extractor,
			final Predicate<A> repeatablePredicate,
			final Predicate<A> groupedPredicate,
			final Function<A, String> groupExtractor,
			final Function<A, String> dbmsExtractor) {
		return this.computeAnnotationHints(annotation,
				new HashSet<>(),
				hintType,
				extractor,
				repeatablePredicate,
				groupedPredicate,
				groupExtractor,
				dbmsExtractor);
	}

	protected <A extends Annotation> Map<String, Object> computeHints(
			final AnnotatedElement element,
			final Class<A> hintType,
			final HintExtractor<A> extractor,
			final Predicate<A> repeatablePredicate,
			final Predicate<A> groupedPredicate,
			final Function<A, String> groupExtractor,
			final Function<A, String> dbmsExtractor) {

		try {
			final Map<String, List<HintValue>> collected = new LinkedHashMap<>();
			for (final Annotation annotation : PCUtils.getUnwrappedAnnotations(element)) {
//				System.err.println(annotation);
				this.collect(annotation,
						collected,
						new HashSet<>(),
						hintType,
						extractor,
						repeatablePredicate,
						groupedPredicate,
						groupExtractor,
						dbmsExtractor,
						null);
			}
			return this.buildHints(collected);
		} catch (final Exception e) {
			throw new DBException("Unable to compute hints for: " + element, e);
		}
	}

	protected <A extends Annotation> void collect(
			final Annotation annotation,
			final Map<String, List<HintValue>> out,
			final Set<Class<?>> visited,
			final Class<A> hintType,
			final HintExtractor<A> extractor,
			final Predicate<A> repeatablePredicate,
			final Predicate<A> groupedPredicate,
			final Function<A, String> groupExtractor,
			final Function<A, String> dbmsExtractor,
			String group)
			throws IllegalAccessException,
				InvocationTargetException {

		final Class<? extends Annotation> type = annotation.annotationType();
		if (!visited.add(type)) {
			return;
		}

		try {
			String dbmsQualifier = "";
			if (type.isAnnotationPresent(DbmsFilter.class)) {
				dbmsQualifier = type.getAnnotation(DbmsFilter.class).value();
			}

			Method qualifierMethod = null;
			for (final Method m : type.getDeclaredMethods()) {
				if (m.isAnnotationPresent(DbmsFilter.class)) {
					if (qualifierMethod != null) {
						throw new IllegalArgumentException("Multiple @DbmsFilter methods on " + type);
					}
					qualifierMethod = m;
				}
			}

			if (qualifierMethod != null) {
				dbmsQualifier = (String) qualifierMethod.invoke(annotation);
			}

			if (!dbmsQualifier.isEmpty() && !this.matchesDbmsQualifier(dbmsQualifier)) {
				return;
			}

			final A typeHint = type.isAnnotationPresent(hintType) ? type.getAnnotation(hintType) : null;
			final boolean repeatable = typeHint != null && repeatablePredicate.test(typeHint);
			final boolean grouped = typeHint != null && groupedPredicate.test(typeHint);

			if (grouped) {
				group = groupExtractor.apply(typeHint);
			}

//			System.err.println(hintType + " <> " + type);
			if (hintType == type) {
//				System.err.println("found self: " + annotation);
				final A anno = hintType.cast(annotation);
				if (!this.matchesDbmsQualifier(dbmsExtractor.apply(anno))) {
					return;
				}
				extractor.extract(anno, null, out, dbmsQualifier, repeatable, group);
				return;
			}

			for (final Annotation meta : PCUtils.getUnwrappedAnnotations(type)) {
				if (hintType.isInstance(meta)) {
					extractor.extract(hintType.cast(meta), null, out, dbmsQualifier, repeatable, group);
				} else {
					this.collect(meta,
							out,
							visited,
							hintType,
							extractor,
							repeatablePredicate,
							groupedPredicate,
							groupExtractor,
							dbmsExtractor,
							group);
				}
			}

			for (final Method method : type.getDeclaredMethods()) {
				if (method == qualifierMethod) {
					continue;
				}

				final Object value = method.invoke(annotation);
				if (this.isEmptyValue(value)) {
//					System.err.println("empty value: " + annotation + " " + method);
					continue;
				}

				final List<A> methodHints = new ArrayList<>();
				for (final Annotation meta : PCUtils.getUnwrappedAnnotations(method)) {
					if (hintType.isInstance(meta)) {
						methodHints.add(hintType.cast(meta));
					}
				}

				if (value instanceof Annotation) {
					if (!methodHints.isEmpty()) {
						final Map<String, Object> nested = this.computeAnnotationHints((Annotation) value,
								visited,
								hintType,
								extractor,
								repeatablePredicate,
								groupedPredicate,
								groupExtractor,
								dbmsExtractor);

						for (final A hint : methodHints) {
							if (!this.matchesDbmsQualifier(dbmsExtractor.apply(hint))) {
								continue;
							}
							extractor.extract(hint, nested, out, dbmsQualifier, repeatable, group);
						}
					} else {
						this.collect((Annotation) value,
								out,
								visited,
								hintType,
								extractor,
								repeatablePredicate,
								groupedPredicate,
								groupExtractor,
								dbmsExtractor,
								group);
					}
				} else if (value instanceof Annotation[]) {
					if (!methodHints.isEmpty()) {
						final List<Map<String, Object>> nested = new ArrayList<>();
						for (final Annotation a : (Annotation[]) value) {
							nested.add(this.computeAnnotationHints(a,
									visited,
									hintType,
									extractor,
									repeatablePredicate,
									groupedPredicate,
									groupExtractor,
									dbmsExtractor));
						}

						for (final A hint : methodHints) {
							if (!this.matchesDbmsQualifier(dbmsExtractor.apply(hint))) {
								continue;
							}
							extractor.extract(hint, nested, out, dbmsQualifier, repeatable, group);
						}
					} else {
						for (final Annotation a : (Annotation[]) value) {
							this.collect(a,
									out,
									visited,
									hintType,
									extractor,
									repeatablePredicate,
									groupedPredicate,
									groupExtractor,
									dbmsExtractor,
									group);
						}
					}
				} else {
					for (final A hint : methodHints) {
						if (!this.matchesDbmsQualifier(dbmsExtractor.apply(hint))) {
							continue;
						}
						extractor.extract(hint, value, out, dbmsQualifier, repeatable, group);
					}
				}
			}

		} finally {
			visited.remove(type);
		}
	}

	public Map<String, Object> computeColumnHints(final Field field) {
		return this.computeColumnHints((AnnotatedElement) field);
	}

	public Map<String, Object> computeColumnHints(final Parameter parameter) {
		return this.computeColumnHints((AnnotatedElement) parameter);
	}

	public Map<String, Object> computeColumnHints(final AnnotatedElement element) {
		if (!(element instanceof Field || element instanceof Parameter)) {
			throw new UnsupportedOperationException(element.getClass() + " not supported.");
		}
		return this.computeHints(element,
				ColumnHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)),
				ColumnHint::repeatable,
				ColumnHint::grouped,
				ColumnHint::type,
				ColumnHint::dbms);
	}

	public Map<String, Object> computeTypeHints(final Field field) {
		return this.computeTypeHints(field.getAnnotatedType());
	}

	public Map<String, Object> computeTypeHints(final Parameter parameter) {
		return this.computeTypeHints(parameter.getAnnotatedType());
	}

	public Map<String, Object> computeTypeHints(final AnnotatedType element) {
		return this.computeHints(element,
				TypeHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)),
				TypeHint::repeatable,
				TypeHint::grouped,
				TypeHint::type,
				TypeHint::dbms);
	}

	public Map<String, Object> computeQueryableHints(final Class<?> element) {
		return this.computeHints(element,
				QueryableHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)),
				QueryableHint::repeatable,
				QueryableHint::grouped,
				QueryableHint::type,
				QueryableHint::dbms);
	}

}
