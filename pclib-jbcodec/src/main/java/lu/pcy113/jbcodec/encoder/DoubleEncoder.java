package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class DoubleEncoder extends DefaultObjectEncoder<Double> {

	@Override
	public ByteBuffer encode(boolean head, Double obj) {
		ByteBuffer bb = ByteBuffer.allocate(8 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putDouble(obj);

		bb.flip();
		return bb;
	}
	
	@Override
	public int estimateSize(boolean head, Double obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Double.BYTES;
	}

}
