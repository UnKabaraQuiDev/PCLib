package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class ArrayDecoder implements Decoder<Object[]> {

	private CodecManager cm = null;
	private short header;

	public CodecManager codecManager() {
		return cm;
	}

	public short header() {
		return header;
	}

	public Class<?> type() {
		return null;
	}

	public String register(CodecManager cm, short header) {
		verifyRegister();

		this.cm = cm;
		this.header = header;

		return "Array"; // type().getName();
	}

	public Object[] decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		int length = bb.getInt();

		Object[] array = new Object[length];
		for (int i = 0; i < length; i++) {
			array[i] = cm.decode(bb);
		}
		return array;
	}

}