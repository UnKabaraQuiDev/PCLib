package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDecoder extends DefaultObjectDecoder<UUID> {

	@Override
	public UUID decode(boolean head, ByteBuffer bb) {
		super.verifyHeader(head, bb);
		
		return new UUID(bb.getLong(), bb.getLong());
	}

}
