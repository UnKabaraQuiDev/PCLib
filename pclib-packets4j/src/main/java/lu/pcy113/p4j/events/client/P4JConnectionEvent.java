package lu.pcy113.p4j.events.client;

import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.events.P4JEvent;
import lu.pcy113.p4j.socket.P4JInstance.P4JClientInstance;
import lu.pcy113.p4j.socket.P4JInstance.P4JServerInstance;

public interface P4JConnectionEvent extends P4JEvent {

	P4JEndPoint getEndPoint();

	P4JServerInstance getServer();

	P4JClientInstance getClient();

	public static class ClientConnectedEvent implements P4JConnectionEvent {

		private P4JEndPoint endPoint;
		private P4JClientInstance client;
		private P4JServerInstance server;

		public ClientConnectedEvent(P4JEndPoint endPoint, P4JClientInstance client, P4JServerInstance server) {
			this.endPoint = endPoint;
			this.client = client;
			this.server = server;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JClientInstance getClient() {
			return client;
		}

		@Override
		public P4JServerInstance getServer() {
			return server;
		}

	}

	public static class ClientDisconnectedEvent implements P4JConnectionEvent {

		private P4JEndPoint endPoint;
		private Exception exception;
		private P4JServerInstance server;
		private P4JClientInstance client;

		public ClientDisconnectedEvent(P4JEndPoint endPoint, Exception e, P4JServerInstance server, P4JClientInstance client) {
			this.endPoint = endPoint;
			this.exception = e;
			this.server = server;
			this.client = client;
		}

		public ClientDisconnectedEvent(P4JEndPoint endPoint, P4JServerInstance server, P4JClientInstance client) {
			this.endPoint = endPoint;
			this.server = server;
			this.client = client;
		}

		public boolean isFail() {
			return exception != null;
		}

		public Exception getException() {
			return exception;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JClientInstance getClient() {
			return client;
		}

		@Override
		public P4JServerInstance getServer() {
			return server;
		}

	}

}
