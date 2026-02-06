package examples.class_object;

import lu.pcy113.jbcodec.other.ObjectSerializable;
import lu.pcy113.jbcodec.other.ObjectSerializableInit;

public class SerializableObject implements ObjectSerializable {

	@ObjectSerializableInit
	public static SerializableObject init() {
		return new SerializableObject();
	}

}
