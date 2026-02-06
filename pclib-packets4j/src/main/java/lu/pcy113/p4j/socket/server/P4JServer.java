package lu.pcy113.p4j.socket.server;

import java.io.Closeable;
import java.io.IOException;
import java.lang.Thread.State;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.compress.CompressionManager;
import lu.pcy113.p4j.crypto.EncryptionManager;
import lu.pcy113.p4j.events.P4JEvent;
import lu.pcy113.p4j.events.server.ServerClosedEvent;
import lu.pcy113.p4j.exceptions.P4JServerException;
import lu.pcy113.p4j.packets.HeartbeatPacket;
import lu.pcy113.p4j.packets.PacketManager;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.P4JInstance.P4JServerInstance;
import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.listener.EventDispatcher;
import lu.pcy113.pclib.listener.EventManager;
import lu.pcy113.pclib.listener.SyncEventManager;

public class P4JServer implements P4JServerInstance, EventDispatcher, Closeable, Runnable {

	public static int MAX_PACKET_SIZE = 2048;

	private ServerStatus serverStatus = ServerStatus.PRE;

	private EventManager eventManager = new SyncEventManager();

	private CodecManager codec;
	private EncryptionManager encryption;
	private CompressionManager compression;
	private PacketManager packets = new PacketManager(this);
	private ClientManager clientManager;

	private InetSocketAddress localInetSocketAddress;

	private Thread thread;
	private ServerSocketChannel serverSocketChannel;
	private Selector serverSocketSelector;

	private Function<Runnable, Thread> threadFactory = Thread::new;

	/**
	 * Default constructor for a P4JServer, creates a default {@link ClientManager}
	 * bound to this server instance.
	 * 
	 * @param CodecManager       the server codec manager
	 * @param EntryptionManager  the server encryption manager
	 * @param CompressionManager the server compression manager
	 */
	public P4JServer(CodecManager cm, EncryptionManager em, CompressionManager com) {
		this.codec = cm;
		this.encryption = em;
		this.compression = com;
		this.clientManager = new ClientManager(this);

		this.packets.register(HeartbeatPacket.class, 0x00);

		MAX_PACKET_SIZE = PCUtils.parseInteger(System.getProperty("P4J_maxPacketSize"), MAX_PACKET_SIZE);
	}

	/*
	 * public P4JServer(CodecManager cm, EncryptionManager em, CompressionManager
	 * com, ClientManager clientManager) { this.codec = cm; this.encryption = em;
	 * this.compression = com; this.clientManager = clientManager; }
	 */

	/**
	 * Binds the current server to the local address.
	 * 
	 * @param InetSocketAddress the local address to bind to
	 * @throws IOException        if the {@link ServerSocketChannel} or
	 *                            {@link Selector} cannot be opened or bound
	 * @throws P4JServerException if the server is already bound
	 */
	public synchronized void bind(InetSocketAddress isa) throws IOException {
		if (!(serverStatus.equals(ServerStatus.PRE) || serverStatus.equals(ServerStatus.CLOSED))) {
			throw new P4JServerException("Server already bound.");
		}

		serverSocketSelector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocketChannel.socket().bind(isa);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(serverSocketSelector, SelectionKey.OP_ACCEPT);
		serverStatus = ServerStatus.BOUND;

		this.localInetSocketAddress = new InetSocketAddress(serverSocketChannel.socket().getInetAddress(), serverSocketChannel.socket().getLocalPort());

		this.thread = threadFactory.apply(this);
		this.thread.setName("P4JServer@" + localInetSocketAddress.getHostString() + ":" + localInetSocketAddress.getPort());
	}

	public void run() {
		try {
			while (serverStatus.equals(ServerStatus.ACCEPTING)) {
				serverSocketSelector.select();

				Set<SelectionKey> selectedKeys = serverSocketSelector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();

					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						// Accept a new client connection
						ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
						SocketChannel clientChannel = serverChannel.accept();
						clientChannel.configureBlocking(false);

						// Register the client socket channel with the selector for reading
						clientChannel.register(serverSocketSelector, SelectionKey.OP_READ);

						clientManager.register(clientChannel);
					} else if (key.isReadable()) {
						// Read data from a client socket channel
						SocketChannel clientChannel = (SocketChannel) key.channel();
						clientManager.get(clientChannel).read();

					} else if (key.isWritable()) {
						SocketChannel clientChannel = (SocketChannel) key.channel();
						clientChannel.socket().getOutputStream().flush();
					}

					keyIterator.remove();
				}
			}
		} catch (ClosedByInterruptException e) {
			Thread.interrupted(); // clear interrupt flag
			// ignore because triggered in #close()
		} catch (Exception e) {
			throw new P4JServerException(e);
		}
	}

	/**
	 * Sends the packet to all the connected clients.
	 */
	public synchronized void broadcast(S2CPacket<?> packet) {
		Objects.requireNonNull(packet);

		for (ServerClient sc : clientManager.getAllClients()) {
			sc.write(packet);
		}
	}

	/**
	 * Sends the packet to all the connected clients.
	 */
	public synchronized void broadcast(List<S2CPacket<?>> packets) {
		Objects.requireNonNull(packets);

		if (packets.isEmpty()) {
			return;
		}

		for (ServerClient sc : clientManager.getAllClients()) {
			for (S2CPacket<?> packet : packets) {
				sc.write(packet);
			}
		}
	}

	/**
	 * Sends the packet provided by the supplier to all the connected clients.
	 */
	public synchronized void broadcast(Function<ServerClient, S2CPacket<?>> packetSupplier) {
		for (ServerClient sc : clientManager.getAllClients()) {
			sc.write(packetSupplier.apply(sc));
		}
	}

	/**
	 * Iterates over all the connected clients and sends the specified packet if the
	 * predicate's condition is met.
	 */
	public synchronized void broadcastIf(S2CPacket<?> packet, Predicate<ServerClient> condition) {
		Objects.requireNonNull(packet);

		for (ServerClient sc : clientManager.getAllClients()) {
			if (condition.test(sc)) {
				sc.write(packet);
			}
		}
	}

	/**
	 * Iterates over all the connected clients and sends the specified packet(s) if
	 * the predicate's condition is met.
	 */
	public synchronized void broadcastIf(List<S2CPacket<?>> packets, Predicate<ServerClient> condition) {
		Objects.requireNonNull(packets);

		if (packets.isEmpty()) {
			return;
		}

		for (ServerClient sc : clientManager.getAllClients()) {
			if (condition.test(sc)) {
				for (S2CPacket<?> packet : packets) {
					sc.write(packet);
				}
			}
		}
	}

	/**
	 * Iterates over all the connected clients and sends the packet provided by the
	 * supplier, if the predicate's condition is met.
	 */
	public synchronized void broadcastIf(Function<ServerClient, S2CPacket<?>> packetSupplier, Predicate<ServerClient> condition) {
		for (ServerClient sc : clientManager.getAllClients()) {
			if (condition.test(sc)) {
				sc.write(packetSupplier.apply(sc));
			}
		}
	}

	/**
	 * Sets the server socket in client accept mode.<br>
	 * The server will accept all future incoming client connections.
	 * 
	 * @throws P4JServerException if the server socket is closed
	 */
	public void setAccepting() {
		if (serverStatus.equals(ServerStatus.CLOSED))
			throw new P4JServerException("Cannot set closed server socket in client accept mode.");

		serverStatus = ServerStatus.ACCEPTING;

		if (!this.thread.isAlive()) {
			this.thread.start();
		}
	}

	public void disconnectAll() {
		for (ServerClient sc : clientManager.getAllClients()) {
			sc.disconnect();
		}
	}

	/**
	 * Closes the server socket.<br>
	 * The server will no longer accept new client connections, all clients will be
	 * forcefully disconnected and the local port is released.
	 * 
	 * @throws P4JServerException if the server socket is already closed
	 */
	@Override
	public synchronized void close() {
		if (serverStatus.equals(ServerStatus.CLOSED) || serverStatus.equals(ServerStatus.PRE))
			throw new P4JServerException("Cannot close not started server socket.");

		try {
			serverStatus = ServerStatus.CLOSING;
			this.thread.interrupt();
			serverSocketChannel.close();
			serverStatus = ServerStatus.CLOSED;

			dispatchEvent(new ServerClosedEvent(this));
		} catch (IOException e) {
			throw new P4JServerException(e);
		}
	}

	/**
	 * Sets the server socket in client refuse mode.<br>
	 * The server will refuse all future incoming client connections but keep
	 * current connections alive.
	 * 
	 * @throws P4JServerException if the server socket is closed.
	 */
	public void setRefusing() {
		if (serverStatus.equals(ServerStatus.CLOSED))
			throw new P4JServerException("Cannot set closed server socket in client refuse mode.");

		this.serverStatus = ServerStatus.REFUSING;
	}

	public void registerPacket(Class<?> p, int id) {
		packets.register(p, id);
	}

	public void dispatchEvent(P4JEvent event) {
		if (eventManager == null)
			return;

		eventManager.dispatch(event, this);
	}

	// ----- thread delegated methods

	public final void join() throws InterruptedException {
		thread.join();
	}

	public final void join(long millis, int nanos) throws InterruptedException {
		thread.join(millis, nanos);
	}

	public final void join(long millis) throws InterruptedException {
		thread.join(millis);
	}

	// ----- thread delegated methods

	public State getState() {
		return thread.getState();
	}

	public final boolean isAlive() {
		return thread.isAlive();
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public InetSocketAddress getLocalInetSocketAddress() {
		return localInetSocketAddress;
	}

	public Collection<ServerClient> getConnectedClients() {
		return clientManager.getAllClients();
	}

	/**
	 * @return the local port bound to the server or -1 if the server is closed
	 */
	public int getPort() {
		return (serverSocketChannel != null && serverSocketChannel.socket() != null ? serverSocketChannel.socket().getLocalPort() : -1);
	}

	public CodecManager getCodec() {
		return codec;
	}

	public EncryptionManager getEncryption() {
		return encryption;
	}

	public CompressionManager getCompression() {
		return compression;
	}

	public PacketManager getPackets() {
		return packets;
	}

	public ClientManager getClientManager() {
		return clientManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setCodec(CodecManager codec) {
		this.codec = codec;
	}

	public void setEncryption(EncryptionManager encryption) {
		this.encryption = encryption;
	}

	public void setCompression(CompressionManager compression) {
		this.compression = compression;
	}

	public void setPackets(PacketManager packets) {
		this.packets = packets;
	}

	public void setClientManager(ClientManager clientManager) {
		this.clientManager = clientManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JServerInstance.super.getEndPoint();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "#" + hashCode() + "@{local=" + localInetSocketAddress + ", status=" + serverStatus + ", thread=" + super.toString() + "}";
	}

}