package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class ArrayDecoder implements Decoder<Object[]> {

	private CodecManager cm = null;
	private short header;

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
		return null;
	}

	@Override
	public String register(final CodecManager cm, final short header) {
		this.verifyRegister();

		this.cm = cm;
		this.header = header;

		return "Array"; // type().getName();
	}

	@Override
	public Object[] decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();

		final Object[] array = new Object[length];
		for (int i = 0; i < length; i++) {
			array[i] = this.cm.decode(bb);
		}
		return array;
	}

}
