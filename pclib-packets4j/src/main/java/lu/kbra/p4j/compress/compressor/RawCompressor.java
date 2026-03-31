package lu.kbra.p4j.compress.compressor;

import java.nio.ByteBuffer;

public class RawCompressor implements Compressor {

	@Override
	public ByteBuffer compress(final ByteBuffer bb) {
		return bb;
	}

}
