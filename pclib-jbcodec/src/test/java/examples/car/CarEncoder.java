package examples.car;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.jbcodec.encoder.DefaultObjectEncoder;

public class CarEncoder extends DefaultObjectEncoder<Car> {

	public CarEncoder() {
		super(Car.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final Car obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));
		if (head) {
			bb.putShort(this.header);
		}

		bb.putInt(obj.amountOfWheels);
		bb.putLong(obj.capacity);
		bb.put(this.cm.encode(true, obj.full));
		bb.put(this.cm.encode(true, obj.name));

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Car obj) {
		// header: 2B
		// amountOfWheels: 4B
		// capacity: 8B
		// full: estimateSize(Boolean)
		// name: estimateSize(String)
		return (head ? CodecManager.HEAD_SIZE : 0) + 4 + 8 + 2 + this.cm.estimateSize(true, obj.full)
				+ this.cm.estimateSize(true, obj.name);
	}

}
