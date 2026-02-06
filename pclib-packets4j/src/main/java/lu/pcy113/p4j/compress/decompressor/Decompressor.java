package lu.pcy113.p4j.compress.decompressor;

import java.nio.ByteBuffer;

public interface Decompressor {

	ByteBuffer decompress(ByteBuffer bb) throws Exception;

}
