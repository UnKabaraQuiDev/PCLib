package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.pair.Pair;
import lu.kbra.pclib.datastructure.pair.Pairs;

public class PairDecoder extends DefaultObjectDecoder<Pair<?, ?>> {

	public PairDecoder() {
		super(Pair.class);
	}

	@Override
	public Pair<?, ?> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final boolean readOnly = this.cm.getDecoderByClass(Boolean.class).decode(false, bb);
		if (readOnly) {
			return Pairs.readOnly(this.cm.decode(bb), this.cm.decode(bb));
		} else {
			return Pairs.pair(this.cm.decode(bb), this.cm.decode(bb));
		}
	}

}
