package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class CharacterEncoder extends DefaultObjectEncoder<Character> {

	@Override
	public ByteBuffer encode(final boolean head, final Character obj) {
		final ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.putChar(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Character obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Character.BYTES;
	}

}
