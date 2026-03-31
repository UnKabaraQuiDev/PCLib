package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class NullDecoder implements Decoder<Object> {

	public CodecManager cm = null;
	public short header;

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

		return "Null";
	}

	@Override
	public Byte decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return null;
	}

}
