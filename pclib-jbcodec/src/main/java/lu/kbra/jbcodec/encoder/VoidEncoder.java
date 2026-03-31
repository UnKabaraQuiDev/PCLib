package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class VoidEncoder extends DefaultObjectEncoder<Void> {

	@Override
	public ByteBuffer encode(final boolean head, final Void obj) {
		final ByteBuffer bb = ByteBuffer.allocate(head ? 2 : 0);
		if (head) {
			bb.putShort(this.header);
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Void obj) {
		return head ? CodecManager.HEAD_SIZE : 0;
	}

}
