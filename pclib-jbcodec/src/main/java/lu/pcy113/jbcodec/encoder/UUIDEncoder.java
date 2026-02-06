package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDEncoder extends DefaultObjectEncoder<UUID> {

	@Override
	public ByteBuffer encode(boolean head, UUID obj) {
		final ByteBuffer bb = ByteBuffer.allocate(16 + (head ? 2 : 0));
		
		super.putHeader(head, bb);
		
		bb.putLong(obj.getMostSignificantBits());
		bb.putLong(obj.getLeastSignificantBits());
		
		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, UUID obj) {
		return super.estimateHeaderSize(head) + 2 * Long.BYTES;
	}

}
