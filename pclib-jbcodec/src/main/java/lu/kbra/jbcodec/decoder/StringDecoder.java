package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringDecoder extends DefaultObjectDecoder<String> {

	private final Charset charset;

	public StringDecoder(final Charset charset) {
		super(String.class);
		this.charset = charset;
	}

	public StringDecoder(final String charset) {
		super(String.class);
		this.charset = Charset.forName(charset);
	}

	@Override
	public String decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();

		final byte[] arr = new byte[length];
		bb.get(arr);

		return new String(arr, this.charset);
	}

	public Charset getCharset() {
		return this.charset;
	}

}
