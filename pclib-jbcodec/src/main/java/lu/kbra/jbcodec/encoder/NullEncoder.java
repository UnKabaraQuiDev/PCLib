package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

public class NullEncoder extends DefaultObjectEncoder<Object> {

	@Override
	public boolean confirmType(final Object obj) {
		return obj == null;
	}

	@Override
	public ByteBuffer encode(final boolean head, final Object obj) {
		final ByteBuffer bb = ByteBuffer.allocate(head ? 2 : 0);

		super.putHeader(head, bb);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Object obj) {
		return super.estimateHeaderSize(head);
	}

}
