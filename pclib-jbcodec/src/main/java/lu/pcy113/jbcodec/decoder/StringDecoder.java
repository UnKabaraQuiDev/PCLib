package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringDecoder extends DefaultObjectDecoder<String> {

	private final Charset charset;

	public StringDecoder(Charset charset) {
		super(String.class);
		this.charset = charset;
	}

	public StringDecoder(String charset) {
		super(String.class);
		this.charset = Charset.forName(charset);
	}

	@Override
	public String decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final int length = bb.getInt();

		final byte[] arr = new byte[length];
		bb.get(arr);

		return new String(arr, charset);
	}

	public Charset getCharset() {
		return charset;
	}

}
