package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class VoidEncoder extends DefaultObjectEncoder<Void> {

	@Override
	public ByteBuffer encode(boolean head, Void obj) {
		ByteBuffer bb = ByteBuffer.allocate((head ? 2 : 0));
		if (head)
			bb.putShort(header);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Void obj) {
		return (head ? CodecManager.HEAD_SIZE : 0);
	}

}
