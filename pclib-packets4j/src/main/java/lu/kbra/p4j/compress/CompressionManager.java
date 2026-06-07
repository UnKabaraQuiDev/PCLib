package lu.kbra.p4j.compress;

import java.nio.ByteBuffer;

import lu.kbra.p4j.compress.compressor.Compressor;
import lu.kbra.p4j.compress.compressor.RawCompressor;
import lu.kbra.p4j.compress.decompressor.Decompressor;
import lu.kbra.p4j.compress.decompressor.RawDecompressor;

public class CompressionManager {

	public static final CompressionManager raw() {
		return new CompressionManager(new RawCompressor(), new RawDecompressor());
	}

	private Compressor compressor;

	private Decompressor decompressor;

	public CompressionManager(final Compressor e, final Decompressor d) {
		this.compressor = e;
		this.decompressor = d;
	}

	public ByteBuffer compress(final ByteBuffer b) throws Exception {
		return this.compressor.compress(b);
	}

	public ByteBuffer decompress(final ByteBuffer b) throws Exception {
		return this.decompressor.decompress(b);
	}

	public Compressor getCompressor() {
		return this.compressor;
	}

	public Decompressor getDecompressor() {
		return this.decompressor;
	}

	public void setCompressor(final Compressor compressor) {
		this.compressor = compressor;
	}

	public void setDecompressor(final Decompressor decompressor) {
		this.decompressor = decompressor;
	}

}
