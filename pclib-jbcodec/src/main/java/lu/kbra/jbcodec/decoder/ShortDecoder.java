package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ShortDecoder extends DefaultObjectDecoder<Short> {

	@Override
	public Short decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getShort();
	}

}
