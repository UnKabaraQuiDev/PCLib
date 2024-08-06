package lu.pcy113.pclib.impl;

import java.util.Map.Entry;

public interface ConfigurationSection extends Iterable<Entry<String, Object>> {

	Object get(String key);

	String getString(String key);

	boolean getBoolean(String key);

	byte getByte(String key);

	short getShort(String key);

	char getChar(String key);

	int getInt(String key);

	long getLong(String key);

	double getDouble(String key);

	float getFloat(String key);

	boolean hasKey(String key);

	boolean isSection(String key);

	ConfigurationSection getSection(String key);

	void set(String key, Object value);
	
	String toString(int ident);
	
}
