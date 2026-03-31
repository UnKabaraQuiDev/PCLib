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
			return "{path=" + this.path + "}";
		}

	}

	public static class SubTestConfigContainer implements ConfigContainer {

		@ConfigProp("path")
		public String path;

		@ConfigProp("sub")
		public SubSubTestConfigContainer sub = new SubSubTestConfigContainer();

		@Override
		public String toString() {
			return "{path=" + this.path + ", sub=" + this.sub + "}";
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
			return "{test=" + this.test + ", integer=" + this.integer + ", dooble=" + this.dooble + ", sub=" + this.sub + "}";
		}

	}

	@Test
	public void loadProperties() throws FileNotFoundException, IOException {
		final TestConfigContainer config = ConfigLoader.loadFromPropertiesFile(new TestConfigContainer(),
				new File("./src/test/resources/config/props.properties"));

		System.out.println("CONFIG (properties): " + config);

		this.assertConfig(config);
	}

	@Test
	public void loadJSON() throws FileNotFoundException, IOException {
		final TestConfigContainer config = ConfigLoader.loadFromJSONFile(new TestConfigContainer(),
				new File("./src/test/resources/config/props.json"));

		System.out.println("CONFIG (json): " + config);

		this.assertConfig(config);
	}

	private void assertConfig(final ConfigMain.TestConfigContainer config) {
		assert "string".equals(config.test) : "Strings do not match";
		assert config.integer == 12 : "Integers do not match";
		assert config.dooble == 12.2 : "Doubles do not match";
		assert "subpath".equals(config.sub.path) : "Sub Strings do not match";
		assert "sub usb path".equals(config.sub.sub.path) : "Sub Sub Strings do not match";
	}

}
