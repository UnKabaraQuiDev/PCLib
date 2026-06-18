package lu.kbra.pclib.db.connector;

import java.util.function.Supplier;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public interface DataBaseConnectorFactory extends Supplier<DataBaseConnector> {

}
