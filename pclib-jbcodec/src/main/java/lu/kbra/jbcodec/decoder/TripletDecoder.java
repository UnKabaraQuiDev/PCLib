package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.triplet.Triplet;
import lu.kbra.pclib.datastructure.triplet.Triplets;

public class TripletDecoder extends DefaultObjectDecoder<Triplet<?, ?, ?>> {

	public TripletDecoder() {
		super(Triplet.class);
	}

	@Override
	public Triplet<?, ?, ?> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final boolean readOnly = cm.getDecoderByClass(Boolean.class).decode(false, bb);
		if (readOnly) {
			return Triplets.readOnly(cm.decode(bb), cm.decode(bb), cm.decode(bb));
		} else {
			return Triplets.triplet(cm.decode(bb), cm.decode(bb), cm.decode(bb));
		}
	}

}
