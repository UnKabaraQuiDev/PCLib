package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class CharacterEncoder extends DefaultObjectEncoder<Character> {

	@Override
	public ByteBuffer encode(boolean head, Character obj) {
		ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putChar(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Character obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Character.BYTES;
	}

}
