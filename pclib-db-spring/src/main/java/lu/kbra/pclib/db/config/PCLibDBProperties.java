package lu.kbra.pclib.db.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;

@ConfigurationProperties(prefix = "pclib.db")
public class PCLibDBProperties {

	private boolean enabled = false;

	private String protocol;

	private boolean exposeConnector = true;
	private boolean exposeDatabase = true;
	private boolean autoCreate = true;

	private final Mysql mysql = new Mysql();
	private final Sqlite sqlite = new Sqlite();

	public boolean isExposeConnector() {
		return exposeConnector;
	}

	public void setExposeConnector(boolean exposeConnector) {
		this.exposeConnector = exposeConnector;
	}

	public boolean isExposeDatabase() {
		return exposeDatabase;
	}

	public void setExposeDatabase(boolean exposeDatabase) {
		this.exposeDatabase = exposeDatabase;
	}

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Mysql getMysql() {
		return mysql;
	}

	public Sqlite getSqlite() {
		return sqlite;
	}

	public static class Mysql {

		private String username;
		private String password;
		private String host = "localhost";
		private int port = MySQLDataBaseConnector.DEFAULT_PORT;
		private String name;

		private String characterSet = MySQLDataBaseConnector.DEFAULT_CHARSET;
		private String collation = MySQLDataBaseConnector.DEFAULT_COLLATION;
		private String engine = MySQLDataBaseConnector.DEFAULT_ENGINE;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCharacterSet() {
			return characterSet;
		}

		public void setCharacterSet(String characterSet) {
			this.characterSet = characterSet;
		}

		public String getCollation() {
			return collation;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public String getEngine() {
			return engine;
		}

		public void setEngine(String engine) {
			this.engine = engine;
		}

		@Override
		public String toString() {
			return "Mysql@" + System.identityHashCode(this) + " [username=" + username + ", password=" + password + ", host=" + host
					+ ", port=" + port + ", name=" + name + ", characterSet=" + characterSet + ", collation=" + collation + ", engine="
					+ engine + "]";
		}

	}

	public static class Sqlite {

		private String dirPath = ".";
		private String name;

		public String getDirPath() {
			return dirPath;
		}

		public void setDirPath(String dirPath) {
			this.dirPath = dirPath;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Sqlite@" + System.identityHashCode(this) + " [dirPath=" + dirPath + ", name=" + name + "]";
		}

	}

	@Override
	public String toString() {
		return "PCLibDBProperties@" + System.identityHashCode(this) + " [enabled=" + enabled + ", protocol=" + protocol
				+ ", exposeConnector=" + exposeConnector + ", exposeDatabase=" + exposeDatabase + ", autoCreate=" + autoCreate + ", mysql="
				+ mysql + ", sqlite=" + sqlite + "]";
	}

}
