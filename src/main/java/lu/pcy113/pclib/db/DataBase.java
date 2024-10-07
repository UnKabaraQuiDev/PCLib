package lu.pcy113.pclib.db;

public class DataBase {

	private DataBaseConnector connector;

	public DataBase(DataBaseConnector connector) {
		this.connector = connector;
	}

	public DataBaseConnector getConnector() {
		return connector;
	}

}
