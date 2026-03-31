package lu.kbra.jbcodec.dencoder;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.jbcodec.decoder.Decoder;
import lu.kbra.jbcodec.encoder.Encoder;

public abstract class DefaultObjectDEncoder<T> implements Encoder<T>, Decoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected Class<?> clazz;

	public DefaultObjectDEncoder(final Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectDEncoder() {
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

	public String name() {
		return this.type().getName();
	}

	@Override
	public String register(final CodecManager cm, final short header) {
		// verifyRegister(); don't verify register because its registered twice (encoder
		// + decoder)
		if (this.cm != null) {
			return this.name();
		}

		this.cm = cm;
		this.header = header;

		return this.name();
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
