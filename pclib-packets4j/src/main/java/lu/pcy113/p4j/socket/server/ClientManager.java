package lu.pcy113.p4j.socket.server;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;

public class ClientManager {

	private P4JServer server;

	private Function<SocketChannel, ServerClient> clientCreationCallback;
	private HashMap<SocketChannel, ServerClient> clients = new HashMap<>();

	/**
	 * Creates a default {@link ClientManager} bound to server instance.<br>
	 * This ClientManager creates {@link ServerClient}.
	 * 
	 * @param P4JServer the server
	 */
	public ClientManager(P4JServer server) {
		this(server, (SocketChannel sc) -> new ServerClient(sc, server));
	}

	/**
	 * Creates a custom {@link ClientManager} bound to server instance.<br>
	 * This ClientManager uses the given consumer to create new {@link ServerClient}
	 * instances.
	 * 
	 * @param P4JServer the server
	 * @param Function  the consumer to create new {@link ServerClient} instances
	 *                  from a {@link SocketChannel}
	 */
	public ClientManager(P4JServer server, Function<SocketChannel, ServerClient> clientCreationCallback) {
		this.server = server;
		this.clientCreationCallback = clientCreationCallback;
	}

	/**
	 * Register a new SocketChannel and create a new ServerClient instance using the
	 * ClientManager's consumer.
	 * 
	 * @param SocketChannel the client' socket channel
	 */
	public void register(SocketChannel sc) {
		ServerClient sclient = clientCreationCallback.apply(sc);
		registerClient(sclient);
		server.dispatchEvent(new ClientConnectedEvent(P4JEndPoint.SERVER_CLIENT, sclient, server));
	}

	/**
	 * @param SocketChannel the client' socket channel
	 * @return The {@link ServerClient} for the given {@link SocketChannel}
	 */
	public ServerClient get(SocketChannel clientChannel) {
		return clients.get(clientChannel);
	}

	/**
	 * @param UUID the {@link ServerClient} UUID
	 * @return The {@link ServerClient} for the given {@link UUID} or null if none
	 *         was found
	 */
	public ServerClient get(UUID uuid) {
		return clients.values().parallelStream().filter(sc -> sc.getUUID().equals(uuid)).findFirst().orElse(null);
	}

	/**
	 * Registers a new {@link ServerClient} instance.
	 * 
	 * @param ServerClient the new {@link ServerClient}
	 */
	protected void registerClient(ServerClient sclient) {
		clients.put(sclient.getSocketChannel(), sclient);
	}

	public Set<SocketChannel> allSockets() {
		return clients.keySet();
	}

	public Collection<ServerClient> getAllClients() {
		return clients.values();
	}

	public Set<Entry<SocketChannel, ServerClient>> all() {
		return clients.entrySet();
	}

	/**
	 * Unregister a {@link ServerClient}
	 */
	public void remove(ServerClient serverClient) {
		clients.remove(serverClient.getSocketChannel());
	}

}
