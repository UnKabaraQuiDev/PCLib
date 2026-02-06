package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class VoidDecoder extends DefaultObjectDecoder<Void> {

	@Override
	public Void decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return null;
	}

}
