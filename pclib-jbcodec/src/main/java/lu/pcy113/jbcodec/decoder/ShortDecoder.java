package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ShortDecoder extends DefaultObjectDecoder<Short> {

	@Override
	public Short decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.getShort();
	}

}
