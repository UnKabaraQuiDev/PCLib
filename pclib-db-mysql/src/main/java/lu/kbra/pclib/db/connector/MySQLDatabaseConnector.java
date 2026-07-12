package lu.kbra.pclib.db.connector;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.exception.DBException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MySQLDatabaseConnector extends ThreadLocalDatabaseConnector {

	public static final int DEFAULT_PORT = 3306;

	public static final String PROTOCOL = MySQLDbmsProvider.DBMS_QUALIFIER_NAME;

	private String username;
	private String password;
	private String host;
	public int port = MySQLDatabaseConnector.DEFAULT_PORT;
	protected String database = null;

	public MySQLDatabaseConnector(final String username, final String password, final String host, final int port) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	@Override
	public MySQLDatabaseConnector clone() {
		return new MySQLDatabaseConnector(this.username, this.password, this.host, this.port, this.database);
	}

	@Override
	public Connection createConnection() throws DBException {
		try {
			return DriverManager.getConnection(this.getURI().toString(), this.username, this.password);
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public String getProtocol() {
		return MySQLDatabaseConnector.PROTOCOL;
	}

	@Override
	public URI getURI() {
		final StringBuilder url = new StringBuilder();
		url.append("jdbc:mysql://").append(this.host).append(":").append(this.port).append("/");

		if (this.database != null && !this.database.isEmpty()) {
			url.append(this.database);
		}

		final Map<String, String> params = new LinkedHashMap<>();

		params.put("connectTimeout", "0");
		params.put("socketTimeout", "0");
		params.put("autoReconnect", "true");

		if (!params.isEmpty()) {
			url.append("?");
			url.append(params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&")));
		}

		return URI.create(url.toString());
	}

	@Override
	public final void setDatabase(final String database) {
		if (this.database != null && this.database.equals(database)) {
			return;
		}
		if (this.database != null && database != null) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " already used by db: " + this.database);
		}
		this.database = database;
	}

}
