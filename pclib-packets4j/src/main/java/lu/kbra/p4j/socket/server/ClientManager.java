package lu.kbra.p4j.socket.server;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import lu.kbra.p4j.P4JEndPoint;
import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;

public class ClientManager {

	private final P4JServer server;

	private final Function<SocketChannel, ServerClient> clientCreationCallback;
	private final HashMap<SocketChannel, ServerClient> clients = new HashMap<>();

	/**
	 * Creates a default {@link ClientManager} bound to server instance.<br>
	 * This ClientManager creates {@link ServerClient}.
	 *
	 * @param P4JServer the server
	 */
	public ClientManager(final P4JServer server) {
		this(server, (final SocketChannel sc) -> new ServerClient(sc, server));
	}

	/**
	 * Creates a custom {@link ClientManager} bound to server instance.<br>
	 * This ClientManager uses the given consumer to create new {@link ServerClient} instances.
	 *
	 * @param P4JServer the server
	 * @param Function  the consumer to create new {@link ServerClient} instances from a
	 *                  {@link SocketChannel}
	 */
	public ClientManager(final P4JServer server, final Function<SocketChannel, ServerClient> clientCreationCallback) {
		this.server = server;
		this.clientCreationCallback = clientCreationCallback;
	}

	/**
	 * Register a new SocketChannel and create a new ServerClient instance using the ClientManager's
	 * consumer.
	 *
	 * @param SocketChannel the client' socket channel
	 */
	public void register(final SocketChannel sc) {
		final ServerClient sclient = this.clientCreationCallback.apply(sc);
		this.registerClient(sclient);
		this.server.dispatchEvent(new ClientConnectedEvent(P4JEndPoint.SERVER_CLIENT, sclient, this.server));
	}

	/**
	 * @param SocketChannel the client' socket channel
	 * @return The {@link ServerClient} for the given {@link SocketChannel}
	 */
	public ServerClient get(final SocketChannel clientChannel) {
		return this.clients.get(clientChannel);
	}

	/**
	 * @param UUID the {@link ServerClient} UUID
	 * @return The {@link ServerClient} for the given {@link UUID} or null if none was found
	 */
	public ServerClient get(final UUID uuid) {
		return this.clients.values().parallelStream().filter(sc -> sc.getUUID().equals(uuid)).findFirst().orElse(null);
	}

	/**
	 * Registers a new {@link ServerClient} instance.
	 *
	 * @param ServerClient the new {@link ServerClient}
	 */
	protected void registerClient(final ServerClient sclient) {
		this.clients.put(sclient.getSocketChannel(), sclient);
	}

	public Set<SocketChannel> allSockets() {
		return this.clients.keySet();
	}

	public Collection<ServerClient> getAllClients() {
		return this.clients.values();
	}

	public Set<Entry<SocketChannel, ServerClient>> all() {
		return this.clients.entrySet();
	}

	/**
	 * Unregister a {@link ServerClient}
	 */
	public void remove(final ServerClient serverClient) {
		this.clients.remove(serverClient.getSocketChannel());
	}

}
