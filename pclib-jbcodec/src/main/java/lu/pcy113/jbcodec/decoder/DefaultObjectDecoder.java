package lu.pcy113.jbcodec.decoder;

import java.lang.reflect.ParameterizedType;

import lu.pcy113.jbcodec.CodecManager;

public abstract class DefaultObjectDecoder<T> implements Decoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected Class<?> clazz;

	public DefaultObjectDecoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectDecoder() {
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

	public String name() {
		return type().getName();
	}

	public String register(CodecManager cm, short header) {
		verifyRegister();

		this.cm = cm;
		this.header = header;

		return name();
	}

}
