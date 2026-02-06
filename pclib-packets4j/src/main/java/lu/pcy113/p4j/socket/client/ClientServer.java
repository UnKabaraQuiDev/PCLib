package lu.pcy113.p4j.socket.client;

import java.net.InetSocketAddress;

import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.socket.P4JInstance.P4JClientServerInstance;

/**
 * Represents the server on the client-side.
 */
public class ClientServer implements P4JClientServerInstance {

	private InetSocketAddress remoteInetSocketAddress;

	public ClientServer(InetSocketAddress inetSocketAddress) {
		this.remoteInetSocketAddress = inetSocketAddress;
	}

	public InetSocketAddress getRemoteInetSocketAddress() {
		return remoteInetSocketAddress;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JClientServerInstance.super.getEndPoint();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "#" + hashCode() + "@{" + remoteInetSocketAddress + "}";
	}

}