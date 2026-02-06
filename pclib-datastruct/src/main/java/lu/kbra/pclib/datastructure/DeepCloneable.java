package lu.kbra.pclib.datastructure;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DeepCloneable extends Cloneable {

	static Field[] getAllFields(final Class<?> clazz) {
		final List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			Collections.addAll(fields, current.getDeclaredFields());
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	DeepCloneable clone();

	default DeepCloneable deepClone() {
		final DeepCloneable dc = clone();

		final Class<?> clazz = dc.getClass();
		final Field[] fields = getAllFields(clazz);

		for (Field f : fields) {
			try {
				f.setAccessible(true);
//				final Type type = f.getGenericType();
				final Class<?> type = f.getType();
				final Object val = f.get(dc);
				if (val != null) {
					if (DeepCloneable.class.isAssignableFrom(type)) {
						f.set(val, ((DeepCloneable) val).deepClone());
					} else if (Cloneable.class.isAssignableFrom(type)) {
						final Method cloneMethod = type.getMethod("clone");
						f.set(cloneMethod.invoke(val), val);
					}
				}
			} catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException e) {
				throw new RuntimeException("Field: " + f.getName(), e);
			}
		}

		return dc;
	}

}
