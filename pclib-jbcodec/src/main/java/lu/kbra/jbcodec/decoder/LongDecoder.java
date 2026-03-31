package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class LongDecoder extends DefaultObjectDecoder<Long> {

	@Override
	public Long decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getLong();
	}

}
