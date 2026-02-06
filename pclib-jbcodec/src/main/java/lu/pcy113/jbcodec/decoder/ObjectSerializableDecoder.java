package lu.pcy113.jbcodec.decoder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.other.ObjectSerializable;
import lu.pcy113.jbcodec.other.ObjectSerializableInit;

public class ObjectSerializableDecoder extends DefaultObjectDecoder<ObjectSerializable> {

	public ObjectSerializableDecoder() {
		super(ObjectSerializable.class);
	}

	@Override
	public ObjectSerializable decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		Class<?> clazz = cm.getDecoderByClass(Class.class).decode(false, bb);

		try {

			if (!ObjectSerializable.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(new ClassCastException());
			}

			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(ObjectSerializableInit.class)) {
					if (!Modifier.isStatic(m.getModifiers())) {
						throw new RuntimeException(new NoSuchMethodException("Method: `" + m.getName() + "` is not static."));
					}

					if (!clazz.isAssignableFrom(m.getReturnType())) {
						throw new RuntimeException(new NoSuchMethodException("Method: `" + m.getName() + "` return type doesn't implement `" + clazz.getName() + "`."));
					}

					m.setAccessible(true);
					return (ObjectSerializable) m.invoke(null);
				}
			}

			if (clazz.isAnnotationPresent(ObjectSerializableInit.class) && clazz.getAnnotationsByType(ObjectSerializableInit.class)[0].ignoreNoSuchMethod()) {
				return null;
			} else {
				throw new RuntimeException(new NoSuchMethodException("No @ObjectSerializableInit static method found in type `" + clazz.getName() + "`"));
			}

		} catch (Exception e) {

			if (!(clazz.isAnnotationPresent(ObjectSerializableInit.class) && clazz.getAnnotationsByType(ObjectSerializableInit.class)[0].ignoreExceptions())) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else {
					throw new RuntimeException(e);
				}
			} else {
				return null;
			}

		}
	}

}
