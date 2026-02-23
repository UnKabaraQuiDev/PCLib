package lu.kbra.pclib.db.connector;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class CloneDataBaseConnectorFactory implements DataBaseConnectorFactory {

	private final DataBaseConnector baseConnector;

	public CloneDataBaseConnectorFactory(DataBaseConnector baseConnector) {
		this.baseConnector = baseConnector;
	}

	@Override
	public DataBaseConnector get() {
		return baseConnector.clone();
	}

}
