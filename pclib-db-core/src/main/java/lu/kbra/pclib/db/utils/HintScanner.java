package lu.kbra.pclib.db.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.ColumnHint;
import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.entry.Grouped;
import lu.kbra.pclib.db.annotations.entry.RepeatableHint;
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
		Object group;
	}

	@FunctionalInterface
	protected interface HintExtractor<A extends Annotation> {
		void extract(A annotation, Object value, Map<String, List<HintValue>> out, String qualifier, boolean repeatable, Object group);
	}

	protected boolean matchesDbmsQualifier(final String dbms) {
		final String trimmed = dbms.trim();
		if (trimmed.isEmpty()) {
			return true;
		}
		return this.dbmsQualifierName.matches(PCUtils.globToRegex(trimmed));
	}

	protected <A extends Annotation> Map<String, Object>
			computeHints(final AnnotatedElement element, final Class<A> hintType, final HintExtractor<A> extractor) {

		try {
			final Map<String, List<HintValue>> collected = new HashMap<>();
			for (final Annotation annotation : PCUtils.getUnwrappedAnnotations(element)) {
				this.collect(annotation, collected, new HashSet<>(), hintType, extractor, null);
			}

			final Map<String, Object> result = new HashMap<>();

			final Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();
			final Map<Object, List<Map<String, Object>>> groupedRepeatable = new LinkedHashMap<>();

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
				result.put("__groups__", grouped);
			}
			if (!groupedRepeatable.isEmpty()) {
				result.put("__repeatableGroups__", groupedRepeatable);
			}

			return result;
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
			Object group)
			throws IllegalAccessException,
				InvocationTargetException {

		final Class<? extends Annotation> type = annotation.annotationType();
		if (!visited.add(type)) {
			return;
		}

		try {
			String qualifier = "";
			if (type.isAnnotationPresent(DbmsFilter.class)) {
				qualifier = type.getAnnotation(DbmsFilter.class).value();
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
				qualifier = (String) qualifierMethod.invoke(annotation);
			}
			if (!qualifier.isEmpty() && !this.matchesDbmsQualifier(qualifier)) {
				return;
			}

			final boolean repeatable = type.isAnnotationPresent(RepeatableHint.class);
			final boolean grouped = type.isAnnotationPresent(Grouped.class);
			if (grouped) {
				group = new Object();
			}

			for (final Annotation meta : PCUtils.getUnwrappedAnnotations(type)) {
				if (hintType.isInstance(meta)) {
					extractor.extract(hintType.cast(meta), null, out, qualifier, repeatable, group);
				} else {
					this.collect(meta, out, visited, hintType, extractor, group);
				}
			}

			for (final Method method : type.getDeclaredMethods()) {
				if (method == qualifierMethod) {
					continue;
				}

				final Object value = method.invoke(annotation);
				for (final Annotation meta : PCUtils.getUnwrappedAnnotations(method)) {
					if (hintType.isInstance(meta)) {
						extractor.extract(hintType.cast(meta), value, out, qualifier, repeatable, group);
					}
				}

				if (value instanceof Annotation) {
					this.collect((Annotation) value, out, visited, hintType, extractor, group);
				} else if (value instanceof Annotation[]) {
					for (final Annotation a : (Annotation[]) value) {
						this.collect(a, out, visited, hintType, extractor, group);
					}
				}
			}

		} finally {
			visited.remove(type);
		}
	}

	public Map<String, Object> computeColumnHints(final Field field) {
		return this.computeColumnHints(field);
	}

	public Map<String, Object> computeColumnHints(final Parameter parameter) {
		return this.computeColumnHints(parameter);
	}

	public Map<String, Object> computeColumnHints(final AnnotatedElement element) {
		if (!(element instanceof Field || element instanceof Parameter)) {
			throw new UnsupportedOperationException(element.getClass() + " not supported.");
		}
		return this.computeHints(element,
				ColumnHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

	public Map<String, Object> computeTypeHints(final AnnotatedType element) {
		return this.computeHints(element,
				TypeHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

	public Map<String, Object> computeQueryableHints(final Class<?> element) {
		return this.computeHints(element,
				QueryableHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

}
