package lu.pcy113.pclib.datastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.impl.ConfigurationSection;

public class YMLConfigurationSection implements ConfigurationSection {

	private Map<String, Object> content;

	public YMLConfigurationSection() {
		this.content = new HashMap<String, Object>();
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return null;
	}

	@Override
	public Object get(String key) {
		if (!hasKey(key)) {
			return null;
		}

		final String[] tokens = key.split("\\.");

		if (tokens.length > 1) {
			if (content.containsKey(tokens[0]) && content.get(tokens[0]) instanceof YMLConfigurationSection) {
				return ((YMLConfigurationSection) content.get(tokens[0])).get(key.substring(key.indexOf('.') + 1));
			} else {
				return false;
			}
		} else {
			return content.get(tokens[0]);
		}
	}

	@Override
	public String getString(String key) {
		return (String) get(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return (boolean) get(key);
	}

	@Override
	public byte getByte(String key) {
		return (byte) get(key);
	}

	@Override
	public short getShort(String key) {
		return (short) get(key);
	}

	@Override
	public char getChar(String key) {
		return (char) get(key);
	}

	@Override
	public int getInt(String key) {
		return (int) get(key);
	}

	@Override
	public long getLong(String key) {
		return (long) get(key);
	}

	@Override
	public double getDouble(String key) {
		return (double) get(key);
	}

	@Override
	public float getFloat(String key) {
		return (float) get(key);
	}

	@Override
	public boolean hasKey(String key) {
		final String[] tokens = key.split("\\.");

		if (tokens.length > 1) {
			if (content.containsKey(tokens[0]) && content.get(tokens[0]) instanceof YMLConfigurationSection) {
				return ((YMLConfigurationSection) content.get(tokens[0])).hasKey(key.substring(key.indexOf('.') + 1));
			} else {
				return false;
			}
		} else {
			return content.containsKey(tokens[0]);
		}
	}

	@Override
	public boolean isSection(String key) {
		return get(key) instanceof YMLConfigurationSection;
	}

	@Override
	public ConfigurationSection getSection(String key) {
		return (YMLConfigurationSection) get(key);
	}

	@Override
	public void set(String key, Object value) {
		final String[] tokens = key.split("\\.");

		if (tokens.length > 1) {
			if (content.containsKey(tokens[0])) {
				if (!(content.get(tokens[0]) instanceof YMLConfigurationSection)) {
					content.put(tokens[0], new YMLConfigurationSection());
				}
			} else {
				content.put(tokens[0], new YMLConfigurationSection());
			}

			((YMLConfigurationSection) content.get(tokens[0])).set(key.substring(key.indexOf('.') + 1), value);
		} else {
			content.put(tokens[0], value);
		}
	}

	@Override
	public String toString(int ident) {
		final String strIdent = PCUtils.repeatString("  ", ident);
		return content.entrySet().stream()
				.map(c -> strIdent + c.getKey() + ": "
						+ (c.getValue() instanceof YMLConfigurationSection ? "\n" + ((YMLConfigurationSection) c.getValue()).toString(ident + 1) : c.getValue() + " (" + c.getValue().getClass().getSimpleName() + ")"))
				.collect(Collectors.joining("\n"));
	}

	@Override
	public String toString() {
		return this.toString(0);
	}

}
