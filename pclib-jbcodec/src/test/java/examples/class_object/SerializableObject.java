package examples.class_object;

import lu.kbra.jbcodec.other.ObjectSerializable;
import lu.kbra.jbcodec.other.ObjectSerializableInit;

public class SerializableObject implements ObjectSerializable {

	@ObjectSerializableInit
	public static SerializableObject init() {
		return new SerializableObject();
	}

}
