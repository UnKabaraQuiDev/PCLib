# PCLib JSON

Helpers for loading and working with JSON-based config files. _Not actively maintained, use Jackson instead_

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-json</artifactId>
</dependency>
````

## What it contains

This module currently focuses on config loading.

Main class:

* `lu.kbra.pclib.config.ConfigLoader`

## Example

```java
import lu.kbra.pclib.config.ConfigLoader;

public class DBConfig implements ConfigContainer {

	@Deprecated
	public final String protocol = "mysql";

	@ConfigProp("username")
	public String username;

	@ConfigProp("password")
	public String password;

	@ConfigProp("host")
	public String host;

}


DBConfig config = ConfigLoader.loadFromJSONFile(new DBConfig(), new File("./config.json"));
DBConfig config = ConfigLoader.loadFromPropertiesFile(new DBConfig(), new File("./properties.json"));
```