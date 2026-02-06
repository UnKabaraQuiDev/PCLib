package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.dencoder.DEncoder;

public interface Encoder<T> extends DEncoder {

	ByteBuffer encode(boolean head, T obj);

	default int estimateSize(boolean head, T obj) {
		throw new UnsupportedOperationException("This method wasn't implements by: " + this.getClass().getName());
	}

	default boolean confirmType(Object obj) {
		return obj != null && obj.getClass().equals(type());
	}

	default boolean confirmClassType(Class<?> clazz) {
		return clazz != null && clazz.equals(type());
	}
}
