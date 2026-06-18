package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.tuple.Triplet;
import lu.kbra.pclib.datastructure.tuple.Triplets;

public class TripletDecoder extends DefaultObjectDecoder<Triplet<?, ?, ?>> {

	public TripletDecoder() {
		super(Triplet.class);
	}

	@Override
	public Triplet<?, ?, ?> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final boolean readOnly = this.cm.getDecoderByClass(Boolean.class).decode(false, bb);
		if (readOnly) {
			return Triplets.readOnly(this.cm.decode(bb), this.cm.decode(bb), this.cm.decode(bb));
		} else {
			return Triplets.triplet(this.cm.decode(bb), this.cm.decode(bb), this.cm.decode(bb));
		}
	}

}
