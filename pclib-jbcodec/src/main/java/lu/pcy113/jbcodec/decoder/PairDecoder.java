package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.datastructure.pair.Pairs;

public class PairDecoder extends DefaultObjectDecoder<Pair<?, ?>> {

	public PairDecoder() {
		super(Pair.class);
	}

	@Override
	public Pair<?, ?> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final boolean readOnly = cm.getDecoderByClass(Boolean.class).decode(false, bb);
		if (readOnly) {
			return Pairs.readOnly(cm.decode(bb), cm.decode(bb));
		} else {
			return Pairs.pair(cm.decode(bb), cm.decode(bb));
		}
	}

}
