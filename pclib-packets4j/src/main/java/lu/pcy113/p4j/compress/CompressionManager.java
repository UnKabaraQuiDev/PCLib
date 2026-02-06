package lu.pcy113.p4j.compress;

import java.nio.ByteBuffer;

import lu.pcy113.p4j.compress.compressor.Compressor;
import lu.pcy113.p4j.compress.compressor.RawCompressor;
import lu.pcy113.p4j.compress.decompressor.Decompressor;
import lu.pcy113.p4j.compress.decompressor.RawDecompressor;

public class CompressionManager {

	private Compressor compressor;
	private Decompressor decompressor;

	public CompressionManager(Compressor e, Decompressor d) {
		this.compressor = e;
		this.decompressor = d;
	}

	public static final CompressionManager raw() {
		return new CompressionManager(new RawCompressor(), new RawDecompressor());
	}

	public ByteBuffer decompress(ByteBuffer b) throws Exception {
		return decompressor.decompress(b);
	}

	public ByteBuffer compress(ByteBuffer b) throws Exception {
		return compressor.compress(b);
	}

	public Compressor getCompressor() {
		return compressor;
	}

	public Decompressor getDecompressor() {
		return decompressor;
	}

	public void setCompressor(Compressor compressor) {
		this.compressor = compressor;
	}

	public void setDecompressor(Decompressor decompressor) {
		this.decompressor = decompressor;
	}

}
