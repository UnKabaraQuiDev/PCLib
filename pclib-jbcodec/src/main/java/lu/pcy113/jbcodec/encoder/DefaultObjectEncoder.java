package lu.pcy113.jbcodec.encoder;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

/**
 * Simplification of an Encoder&lt;T&gt;; this wont work with primitive types (Arrays, etc)
 *
 * @param <T> the type this encoder will encode; has to be the same as passed into the constructor (optional)
 */
public abstract class DefaultObjectEncoder<T> implements Encoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected final Class<?> clazz;

	public DefaultObjectEncoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectEncoder() {
		clazz = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public CodecManager codecManager() {
		return cm;
	}

	public short header() {
		return header;
	}

	public Class<?> type() {
		return clazz;
	}

	public String register(CodecManager cm, short header) {
		verifyRegister();

		this.cm = cm;
		this.header = header;

		return type().getName();
	}
	
	protected void putHeader(boolean head, ByteBuffer bb) {
		if(head) {
			bb.putShort(header);
		}
	}

	public int estimateHeaderSize(boolean head) {
		return head ? CodecManager.HEAD_SIZE : 0;
	}
}
