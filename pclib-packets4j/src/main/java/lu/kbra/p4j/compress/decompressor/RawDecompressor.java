package lu.kbra.p4j.compress.decompressor;

import java.nio.ByteBuffer;

public class RawDecompressor implements Decompressor {

	@Override
	public ByteBuffer decompress(final ByteBuffer bb) {
		return bb;
	}

}
