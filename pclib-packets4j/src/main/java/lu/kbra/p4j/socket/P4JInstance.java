package lu.kbra.p4j.socket;

import lu.kbra.p4j.P4JEndPoint;

public interface P4JInstance {

	P4JEndPoint getEndPoint();

	public interface P4JServerInstance extends P4JInstance {

		@Override
		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.SERVER;
		}

	}

	public interface P4JClientInstance extends P4JInstance {

		@Override
		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.CLIENT;
		}

	}

	/**
	 * Client-side server instance
	 */
	public interface P4JClientServerInstance extends P4JServerInstance {

		@Override
		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.CLIENT_SERVER;
		}

	}

	/**
	 * Server-side client instance
	 */
	public interface P4JServerClientInstance extends P4JClientInstance {

		@Override
		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.SERVER_CLIENT;
		}

	}

}
