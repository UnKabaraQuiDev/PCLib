package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class IntegerDecoder extends DefaultObjectDecoder<Integer> {

	@Override
	public Integer decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.getInt();
	}

}
