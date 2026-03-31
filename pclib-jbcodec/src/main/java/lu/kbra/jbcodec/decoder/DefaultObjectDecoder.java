package lu.kbra.jbcodec.decoder;

import java.lang.reflect.ParameterizedType;

import lu.kbra.jbcodec.CodecManager;

public abstract class DefaultObjectDecoder<T> implements Decoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected Class<?> clazz;

	public DefaultObjectDecoder(final Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectDecoder() {
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
		this.verifyRegister();

		this.cm = cm;
		this.header = header;

		return this.name();
	}

}
