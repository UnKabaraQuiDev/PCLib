package lu.kbra.jbcodec.encoder;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

/**
 * Simplification of an Encoder&lt;T&gt;; this wont work with primitive types (Arrays, etc)
 *
 * @param <T> the type this encoder will encode; has to be the same as passed into the constructor
 *            (optional)
 */
public abstract class DefaultObjectEncoder<T> implements Encoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected final Class<?> clazz;

	public DefaultObjectEncoder(final Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectEncoder() {
		this.clazz = (Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public CodecManager codecManager() {
		return this.cm;
	}

	@Override
	public short header() {
		return this.header;
	}

	@Override
	public Class<?> type() {
		return this.clazz;
	}

	@Override
	public String register(final CodecManager cm, final short header) {
		this.verifyRegister();

		this.cm = cm;
		this.header = header;

		return this.type().getName();
	}

	protected void putHeader(final boolean head, final ByteBuffer bb) {
		if (head) {
			bb.putShort(this.header);
		}
	}

	public int estimateHeaderSize(final boolean head) {
		return head ? CodecManager.HEAD_SIZE : 0;
	}
}
