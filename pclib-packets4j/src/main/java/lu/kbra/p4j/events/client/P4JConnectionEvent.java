package lu.kbra.p4j.events.client;

import lu.kbra.p4j.P4JEndPoint;
import lu.kbra.p4j.events.P4JEvent;
import lu.kbra.p4j.socket.P4JInstance.P4JClientInstance;
import lu.kbra.p4j.socket.P4JInstance.P4JServerInstance;

public interface P4JConnectionEvent extends P4JEvent {

	P4JEndPoint getEndPoint();

	P4JServerInstance getServer();

	P4JClientInstance getClient();

	public static class ClientConnectedEvent implements P4JConnectionEvent {

		private final P4JEndPoint endPoint;
		private final P4JClientInstance client;
		private final P4JServerInstance server;

		public ClientConnectedEvent(final P4JEndPoint endPoint, final P4JClientInstance client, final P4JServerInstance server) {
			this.endPoint = endPoint;
			this.client = client;
			this.server = server;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JClientInstance getClient() {
			return this.client;
		}

		@Override
		public P4JServerInstance getServer() {
			return this.server;
		}

	}

	public static class ClientDisconnectedEvent implements P4JConnectionEvent {

		private final P4JEndPoint endPoint;
		private Exception exception;
		private final P4JServerInstance server;
		private final P4JClientInstance client;

		public ClientDisconnectedEvent(
				final P4JEndPoint endPoint,
				final Exception e,
				final P4JServerInstance server,
				final P4JClientInstance client) {
			this.endPoint = endPoint;
			this.exception = e;
			this.server = server;
			this.client = client;
		}

		public ClientDisconnectedEvent(final P4JEndPoint endPoint, final P4JServerInstance server, final P4JClientInstance client) {
			this.endPoint = endPoint;
			this.server = server;
			this.client = client;
		}

		public boolean isFail() {
			return this.exception != null;
		}

		public Exception getException() {
			return this.exception;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JClientInstance getClient() {
			return this.client;
		}

		@Override
		public P4JServerInstance getServer() {
			return this.server;
		}

	}

}
