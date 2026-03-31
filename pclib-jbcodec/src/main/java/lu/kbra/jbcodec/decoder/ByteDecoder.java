package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ByteDecoder extends DefaultObjectDecoder<Byte> {

	@Override
	public Byte decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.get();
	}

}
