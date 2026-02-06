package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

public class NullEncoder extends DefaultObjectEncoder<Object> {

	@Override
	public boolean confirmType(Object obj) {
		return obj == null;
	}

	@Override
	public ByteBuffer encode(boolean head, Object obj) {
		ByteBuffer bb = ByteBuffer.allocate((head ? 2 : 0));
		
		super.putHeader(head, bb);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Object obj) {
		return super.estimateHeaderSize(head);
	}

}
