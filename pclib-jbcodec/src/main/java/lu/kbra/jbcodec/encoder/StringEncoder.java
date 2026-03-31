package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringEncoder extends DefaultObjectEncoder<String> {

	private final Charset charset;

	public StringEncoder(final Charset charset) {
		super(String.class);
		this.charset = charset;
	}

	public StringEncoder(final String charset) {
		super(String.class);
		this.charset = Charset.forName(charset);
	}

	@Override
	public ByteBuffer encode(final boolean head, final String obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		final byte[] bytes = obj.getBytes(this.charset);

		bb.putInt(bytes.length);
		bb.put(bytes);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final String obj) {
		return super.estimateHeaderSize(head) + Integer.BYTES + obj.getBytes(this.charset).length;
	}

}
