package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class DoublePointer
		extends
		PrimitivePointer<Double> {

	private double value;

	public DoublePointer() {}

	public DoublePointer(double value) {
		this.value = value;
	}

	public double getValue() { return this.value; }

	public void setValue(double value) { this.value = value; }

	@Override
	public ObjectPointer toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
