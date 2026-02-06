package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class ByteEncoder extends DefaultObjectEncoder<Byte> {

	@Override
	public ByteBuffer encode(boolean head, Byte obj) {
		ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.put(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Byte obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Byte.BYTES;
	}

}
