package lu.pcy113.jbcodec.dencoder;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.Decoder;
import lu.pcy113.jbcodec.encoder.Encoder;

public abstract class DefaultObjectDEncoder<T> implements Encoder<T>, Decoder<T> {

	protected CodecManager cm = null;
	protected short header;

	protected Class<?> clazz;

	public DefaultObjectDEncoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	public DefaultObjectDEncoder() {
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
		// verifyRegister(); don't verify register because its registered twice (encoder + decoder)
		if(this.cm != null) {
			return name();
		}
		
		this.cm = cm;
		this.header = header;

		return name();
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
