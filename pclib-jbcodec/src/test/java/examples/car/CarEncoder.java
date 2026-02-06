package examples.car;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.encoder.DefaultObjectEncoder;

public class CarEncoder extends DefaultObjectEncoder<Car> {

	public CarEncoder() {
		super(Car.class);
	}

	@Override
	public ByteBuffer encode(boolean head, Car obj) {
		ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));
		if (head)
			bb.putShort(header);

		bb.putInt(obj.amountOfWheels);
		bb.putLong(obj.capacity);
		bb.put(cm.encode(true, obj.full));
		bb.put(cm.encode(true, obj.name));

		bb.flip();
		return bb;
	}
	
	@Override
	public int estimateSize(boolean head, Car obj) {
		// header: 2B
		// amountOfWheels: 4B
		// capacity: 8B
		// full: estimateSize(Boolean)
		// name: estimateSize(String)
		return (head ? CodecManager.HEAD_SIZE : 0)+ 4 + 8 + 2 + cm.estimateSize(true, obj.full) + cm.estimateSize(true, obj.name);
	}
	
}
