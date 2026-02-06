package examples.car;

public class Car {

	int amountOfWheels;
	long capacity;
	boolean full;
	String name;

	@Override
	public String toString() {
		return amountOfWheels + ", " + capacity + ", " + full + ", " + name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		return toString().equals(obj.toString());
	}
}
