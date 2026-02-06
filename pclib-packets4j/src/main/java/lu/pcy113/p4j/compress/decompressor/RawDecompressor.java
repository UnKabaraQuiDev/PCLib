package lu.pcy113.p4j.compress.decompressor;

import java.nio.ByteBuffer;

public class RawDecompressor implements Decompressor {

	@Override
	public ByteBuffer decompress(ByteBuffer bb) {
		return bb;
	}

}
