package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class VoidDecoder extends DefaultObjectDecoder<Void> {

	@Override
	public Void decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return null;
	}

}
