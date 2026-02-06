package examples.car;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.pclib.PCUtils;

public class CarMain {

	@Test
	public void carTest() {
		// Load the base CodecManager
		CodecManager cm = CodecManager.base();

		// Input value: Long 256
		long in = 256;
		// Encode the input value to a ByteBuffer
		ByteBuffer bb = cm.encode(in);
		// Print out the content of the ByteBuffer
		System.out.println(PCUtils.byteBufferToHexString(bb));
		// Output value: Long 256
		long out = (long) cm.decode(bb);
		System.out.println("(long) in == out: "+(in == out));
		assert in == out;

		// Register the new d/encoders with header=145
		cm.register(new CarEncoder(), new CarDecoder(), (short) 145);

		Car car = new Car();
		car.amountOfWheels = 6;
		car.capacity = 128;
		car.full = true;
		car.name = "Custom Car XXL";
		System.out.println(car.toString());

		ByteBuffer b2 = cm.encode(car);
		System.out.println(PCUtils.byteBufferToHexString(b2));
		Car carOut = (Car) cm.decode(b2);
		System.out.println(carOut.toString());
		System.out.println("(Car) car == carOut: "+car.equals(carOut));;
		assert car.equals(carOut);
	}

}
