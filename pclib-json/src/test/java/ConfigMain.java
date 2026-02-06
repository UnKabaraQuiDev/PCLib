import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.config.ConfigLoader;
import lu.kbra.pclib.config.ConfigLoader.ConfigContainer;
import lu.kbra.pclib.config.ConfigLoader.ConfigProp;

public class ConfigMain {

	public static class SubSubTestConfigContainer implements ConfigContainer {

		@ConfigProp("path")
		public String path;

		@Override
		public String toString() {
			return "{path=" + path + "}";
		}

	}

	public static class SubTestConfigContainer implements ConfigContainer {

		@ConfigProp("path")
		public String path;

		@ConfigProp("sub")
		public SubSubTestConfigContainer sub = new SubSubTestConfigContainer();

		@Override
		public String toString() {
			return "{path=" + path + ", sub=" + sub + "}";
		}

	}

	public static class TestConfigContainer implements ConfigContainer {

		@ConfigProp("test")
		public String test;

		@ConfigProp("integer")
		public int integer;

		@ConfigProp("dooble")
		public double dooble;

		@ConfigProp("sub")
		public SubTestConfigContainer sub = new SubTestConfigContainer();

		@Override
		public String toString() {
			return "{test=" + test + ", integer=" + integer + ", dooble=" + dooble + ", sub=" + sub + "}";
		}

	}

	@Test
	public void loadProperties() throws FileNotFoundException, IOException {
		TestConfigContainer config = ConfigLoader.loadFromPropertiesFile(new TestConfigContainer(), new File("./src/test/resources/config/props.properties"));

		System.out.println("CONFIG (properties): " + config);

		assertConfig(config);
	}

	@Test
	public void loadJSON() throws FileNotFoundException, IOException {
		TestConfigContainer config = ConfigLoader.loadFromJSONFile(new TestConfigContainer(), new File("./src/test/resources/config/props.json"));

		System.out.println("CONFIG (json): " + config);

		assertConfig(config);
	}

	private void assertConfig(ConfigMain.TestConfigContainer config) {
		assert config.test.equals("string") : "Strings do not match";
		assert config.integer == 12 : "Integers do not match";
		assert config.dooble == 12.2 : "Doubles do not match";
		assert config.sub.path.equals("subpath") : "Sub Strings do not match";
		assert config.sub.sub.path.equals("sub usb path") : "Sub Sub Strings do not match";
	}

}