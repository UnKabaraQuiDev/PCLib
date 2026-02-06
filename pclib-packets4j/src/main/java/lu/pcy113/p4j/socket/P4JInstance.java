package lu.pcy113.p4j.socket;

import lu.pcy113.p4j.P4JEndPoint;

public interface P4JInstance {

	public P4JEndPoint getEndPoint();

	public static interface P4JServerInstance extends P4JInstance {

		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.SERVER;
		}

	}

	public static interface P4JClientInstance extends P4JInstance {

		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.CLIENT;
		}

	}

	/**
	 * Client-side server instance
	 */
	public static interface P4JClientServerInstance extends P4JServerInstance {

		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.CLIENT_SERVER;
		}

	}

	/**
	 * Server-side client instance
	 */
	public static interface P4JServerClientInstance extends P4JClientInstance {

		default P4JEndPoint getEndPoint() {
			return P4JEndPoint.SERVER_CLIENT;
		}

	}

}
