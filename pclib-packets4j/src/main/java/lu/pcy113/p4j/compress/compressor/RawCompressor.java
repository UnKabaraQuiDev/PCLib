package lu.pcy113.p4j.compress.compressor;

import java.nio.ByteBuffer;

public class RawCompressor implements Compressor {

	@Override
	public ByteBuffer compress(ByteBuffer bb) {
		return bb;
	}

}
