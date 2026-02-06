package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringEncoder extends DefaultObjectEncoder<String> {

	private final Charset charset;

	public StringEncoder(Charset charset) {
		super(String.class);
		this.charset = charset;
	}

	public StringEncoder(String charset) {
		super(String.class);
		this.charset = Charset.forName(charset);
	}

	@Override
	public ByteBuffer encode(boolean head, String obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		final byte[] bytes = obj.getBytes(charset);
		
		bb.putInt(bytes.length);
		bb.put(bytes);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, String obj) {
		return super.estimateHeaderSize(head) + Integer.BYTES + obj.getBytes(charset).length;
	}

}
