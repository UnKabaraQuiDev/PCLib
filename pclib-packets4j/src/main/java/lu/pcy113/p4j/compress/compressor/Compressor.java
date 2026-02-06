package lu.pcy113.p4j.compress.compressor;

import java.nio.ByteBuffer;

public interface Compressor {

	ByteBuffer compress(ByteBuffer bb) throws Exception;

}
