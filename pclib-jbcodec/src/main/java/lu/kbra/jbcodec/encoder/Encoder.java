package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.dencoder.DEncoder;

public interface Encoder<T> extends DEncoder {

	ByteBuffer encode(boolean head, T obj);

	default int estimateSize(final boolean head, final T obj) {
		throw new UnsupportedOperationException("This method wasn't implements by: " + this.getClass().getName());
	}

	default boolean confirmType(final Object obj) {
		return obj != null && obj.getClass().equals(this.type());
	}

	default boolean confirmClassType(final Class<?> clazz) {
		return clazz != null && clazz.equals(this.type());
	}
}
