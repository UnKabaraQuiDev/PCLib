package lu.pcy113.pclib.config;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.impl.DependsOn;

public final class ConfigLoader {

	@Documented
	@Retention(RUNTIME)
	@Target(FIELD)
	public static @interface ConfigProp {

		String value();

	}

	public interface ConfigContainer {

	}

	@DependsOn("org.json.*")
	public static <T extends ConfigContainer> T loadFromJSONFile(T testConfigContainer, File file) throws FileNotFoundException, IOException {
		return loadFromJSONObject(testConfigContainer, new JSONObject(PCUtils.readStringFile(file)));
	}

	@DependsOn("org.json.*")
	public static <T extends ConfigContainer> T loadFromJSONObject(T testConfigContainer, JSONObject jsonObj) {
		return loadFrom(testConfigContainer, PCUtils.extractKeys(jsonObj), (key) -> PCUtils.getSubKey(key.split("\\."), jsonObj));
	}

	@DependsOn("org.json.*")
	public static <T extends ConfigContainer> T loadFromPropertiesFile(T testConfigContainer, File file) throws FileNotFoundException, IOException {
		Properties ps = new Properties();
		ps.load(new FileReader(file));

		return loadFromProperties(testConfigContainer, ps);
	}

	@DependsOn("org.json.*")
	public static <T extends ConfigContainer> T loadFromProperties(T testConfigContainer, Properties ps) {
		return loadFrom(testConfigContainer, ps.keySet(), (key) -> ps.get(key));
	}

	@DependsOn("org.json.*")
	public static <T extends ConfigContainer> T loadFrom(T config, Iterable<?> keys, Function<String, Object> valueSupplier) {
		Map<String, Field> fields = new HashMap<>();

		Arrays.stream(config.getClass().getFields()).filter((field) -> field.isAnnotationPresent(ConfigProp.class)).forEach((field) -> fields.put(field.getAnnotation(ConfigProp.class).value(), field));

		for (Object kkey : keys) {
			String key = (String) kkey;

			try {

				if (fields.containsKey(key)) {
					Field field = fields.get(key);

					if (valueSupplier.apply(key) instanceof JSONObject && !field.getType().isAssignableFrom(JSONObject.class)) {
						continue;
					}
					field.set(config, getAsType(field.getType(), valueSupplier.apply(key)));
				} else if (key.contains(".")) {
					final String[] tokens = key.split("\\.");

					if (fields.containsKey(tokens[0])) {
						Field field = fields.get(tokens[0]);
						Object value = config;

						int i = 1;

						subDoWhile: {
							do {
								boolean found = false;

								for (Field subField : field.getType().getFields()) {
									if (subField.isAnnotationPresent(ConfigProp.class) && subField.getAnnotation(ConfigProp.class).value().equals(tokens[i])) {
										value = field.get(value);
										field = subField;
										i++;
										found = true;
										break;
									}
								}

								if (!found) {
									break subDoWhile;
									// throw new IllegalArgumentException("No field found for key [" +
									// IntStream.range(0, i).mapToObj((int a) ->
									// tokens[a]).collect(Collectors.joining(".")) + "]");
								}

							} while (i < tokens.length);
						}

						if (valueSupplier.apply(key) instanceof JSONObject && !field.getType().isAssignableFrom(JSONObject.class)) {
							continue;
						}

						field.set(value, getAsType(field.getType(), valueSupplier.apply(key)));
					} else {
						continue;
						// pass
						// throw new IllegalArgumentException("No field found for key [" + tokens[0] +
						// "]");
					}
				}

			} catch (Exception e) {
				throw new RuntimeException("Couldn't set value for key [" + key + "]", e);
			}

		}

		return config;
	}

	private static Object getAsType(Class<?> type, Object obj) {
		Objects.requireNonNull(obj, "Object is null !");

		if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(obj.toString());
		} else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
			return Double.valueOf(obj.toString());
		} else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(obj.toString());
		} else if (type.isAssignableFrom(Path.class)) {
			return Paths.get(obj.toString());
		} else if (type.isAssignableFrom(File.class)) {
			return new File(obj.toString());
		} else if (type.isAssignableFrom(JSONObject.class)) {
			return obj instanceof JSONObject ? (JSONObject) obj : new JSONObject(obj.toString());
		} else if (type.isAssignableFrom(JSONArray.class)) {
			return obj instanceof JSONArray ? (JSONArray) obj : new JSONArray(obj.toString());
		} else {
			return obj;
		}
	}

}
