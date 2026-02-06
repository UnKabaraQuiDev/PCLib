package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ByteDecoder extends DefaultObjectDecoder<Byte> {

	@Override
	public Byte decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.get();
	}

}
