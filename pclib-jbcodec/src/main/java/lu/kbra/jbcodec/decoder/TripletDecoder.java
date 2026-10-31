package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.tuple.Triplet;
import lu.kbra.pclib.datastructure.tuple.Tuples;

public class TripletDecoder extends DefaultObjectDecoder<Triplet<?, ?, ?>> {

	public TripletDecoder() {
		super(Triplet.class);
	}

	@Override
	public Triplet<?, ?, ?> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final boolean readOnly = this.cm.getDecoderByClass(Boolean.class).decode(false, bb);
		if (readOnly) {
			return Tuples.readOnly(this.cm.decode(bb), this.cm.decode(bb), this.cm.decode(bb));
		} else {
			return Tuples.mutable(this.cm.decode(bb), this.cm.decode(bb), this.cm.decode(bb));
		}
	}

}
