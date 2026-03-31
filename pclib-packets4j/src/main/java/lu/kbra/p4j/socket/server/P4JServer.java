package lu.kbra.p4j.socket.server;

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

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.p4j.P4JEndPoint;
import lu.kbra.p4j.compress.CompressionManager;
import lu.kbra.p4j.crypto.EncryptionManager;
import lu.kbra.p4j.events.P4JEvent;
import lu.kbra.p4j.events.server.ServerClosedEvent;
import lu.kbra.p4j.exceptions.P4JServerException;
import lu.kbra.p4j.packets.HeartbeatPacket;
import lu.kbra.p4j.packets.PacketManager;
import lu.kbra.p4j.packets.s2c.S2CPacket;
import lu.kbra.p4j.socket.P4JInstance.P4JServerInstance;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.listener.EventDispatcher;
import lu.kbra.pclib.listener.EventManager;
import lu.kbra.pclib.listener.SyncEventManager;

public class P4JServer implements P4JServerInstance, EventDispatcher, Closeable, Runnable {

	public static int MAX_PACKET_SIZE = 2048;

	private ServerStatus serverStatus = ServerStatus.UNINITIALIZED;

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

	private final Function<Runnable, Thread> threadFactory = Thread::new;

	/**
	 * Default constructor for a P4JServer, creates a default {@link ClientManager} bound to this server
	 * instance.
	 *
	 * @param CodecManager       the server codec manager
	 * @param EntryptionManager  the server encryption manager
	 * @param CompressionManager the server compression manager
	 */
	public P4JServer(final CodecManager cm, final EncryptionManager em, final CompressionManager com) {
		this.codec = cm;
		this.encryption = em;
		this.compression = com;
		this.clientManager = new ClientManager(this);

		this.packets.register(HeartbeatPacket.class, 0x00);

		P4JServer.MAX_PACKET_SIZE = PCUtils.parseInteger(System.getProperty("P4J_maxPacketSize"), P4JServer.MAX_PACKET_SIZE);
	}

	/*
	 * public P4JServer(CodecManager cm, EncryptionManager em, CompressionManager com, ClientManager
	 * clientManager) { this.codec = cm; this.encryption = em; this.compression = com;
	 * this.clientManager = clientManager; }
	 */

	/**
	 * Binds the current server to the local address.
	 *
	 * @param InetSocketAddress the local address to bind to
	 * @throws IOException        if the {@link ServerSocketChannel} or {@link Selector} cannot be
	 *                            opened or bound
	 * @throws P4JServerException if the server is already bound
	 */
	public synchronized void bind(final InetSocketAddress isa) throws IOException {
		if (!(ServerStatus.UNINITIALIZED.equals(this.serverStatus) || ServerStatus.CLOSED.equals(this.serverStatus))) {
			throw new P4JServerException("Server already bound.");
		}

		this.serverSocketSelector = Selector.open();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		this.serverSocketChannel.socket().bind(isa);
		this.serverSocketChannel.configureBlocking(false);
		this.serverSocketChannel.register(this.serverSocketSelector, SelectionKey.OP_ACCEPT);
		this.serverStatus = ServerStatus.BOUND;

		this.localInetSocketAddress = new InetSocketAddress(this.serverSocketChannel.socket().getInetAddress(),
				this.serverSocketChannel.socket().getLocalPort());

		this.thread = this.threadFactory.apply(this);
		this.thread.setName("P4JServer@" + this.localInetSocketAddress.getHostString() + ":" + this.localInetSocketAddress.getPort());
	}

	@Override
	public void run() {
		try {
			while (ServerStatus.ACCEPTING.equals(this.serverStatus)) {
				this.serverSocketSelector.select();

				final Set<SelectionKey> selectedKeys = this.serverSocketSelector.selectedKeys();
				final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					final SelectionKey key = keyIterator.next();

					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						// Accept a new client connection
						final ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
						final SocketChannel clientChannel = serverChannel.accept();
						clientChannel.configureBlocking(false);

						// Register the client socket channel with the selector for reading
						clientChannel.register(this.serverSocketSelector, SelectionKey.OP_READ);

						this.clientManager.register(clientChannel);
					} else if (key.isReadable()) {
						// Read data from a client socket channel
						final SocketChannel clientChannel = (SocketChannel) key.channel();
						this.clientManager.get(clientChannel).read();

					} else if (key.isWritable()) {
						final SocketChannel clientChannel = (SocketChannel) key.channel();
						clientChannel.socket().getOutputStream().flush();
					}

					keyIterator.remove();
				}
			}
		} catch (final ClosedByInterruptException e) {
			Thread.interrupted(); // clear interrupt flag
			// ignore because triggered in #close()
		} catch (final Exception e) {
			throw new P4JServerException(e);
		}
	}

	/**
	 * Sends the packet to all the connected clients.
	 */
	public synchronized void broadcast(final S2CPacket<?> packet) {
		Objects.requireNonNull(packet);

		for (final ServerClient sc : this.clientManager.getAllClients()) {
			sc.write(packet);
		}
	}

	/**
	 * Sends the packet to all the connected clients.
	 */
	public synchronized void broadcast(final List<S2CPacket<?>> packets) {
		Objects.requireNonNull(packets);

		if (packets.isEmpty()) {
			return;
		}

		for (final ServerClient sc : this.clientManager.getAllClients()) {
			for (final S2CPacket<?> packet : packets) {
				sc.write(packet);
			}
		}
	}

	/**
	 * Sends the packet provided by the supplier to all the connected clients.
	 */
	public synchronized void broadcast(final Function<ServerClient, S2CPacket<?>> packetSupplier) {
		for (final ServerClient sc : this.clientManager.getAllClients()) {
			sc.write(packetSupplier.apply(sc));
		}
	}

	/**
	 * Iterates over all the connected clients and sends the specified packet if the predicate's
	 * condition is met.
	 */
	public synchronized void broadcastIf(final S2CPacket<?> packet, final Predicate<ServerClient> condition) {
		Objects.requireNonNull(packet);

		for (final ServerClient sc : this.clientManager.getAllClients()) {
			if (condition.test(sc)) {
				sc.write(packet);
			}
		}
	}

	/**
	 * Iterates over all the connected clients and sends the specified packet(s) if the predicate's
	 * condition is met.
	 */
	public synchronized void broadcastIf(final List<S2CPacket<?>> packets, final Predicate<ServerClient> condition) {
		Objects.requireNonNull(packets);

		if (packets.isEmpty()) {
			return;
		}

		for (final ServerClient sc : this.clientManager.getAllClients()) {
			if (condition.test(sc)) {
				for (final S2CPacket<?> packet : packets) {
					sc.write(packet);
				}
			}
		}
	}

	/**
	 * Iterates over all the connected clients and sends the packet provided by the supplier, if the
	 * predicate's condition is met.
	 */
	public synchronized void broadcastIf(
			final Function<ServerClient, S2CPacket<?>> packetSupplier,
			final Predicate<ServerClient> condition) {
		for (final ServerClient sc : this.clientManager.getAllClients()) {
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
		if (ServerStatus.CLOSED.equals(this.serverStatus)) {
			throw new P4JServerException("Cannot set closed server socket in client accept mode.");
		}

		this.serverStatus = ServerStatus.ACCEPTING;

		if (!this.thread.isAlive()) {
			this.thread.start();
		}
	}

	public void disconnectAll() {
		for (final ServerClient sc : this.clientManager.getAllClients()) {
			sc.disconnect();
		}
	}

	/**
	 * Closes the server socket.<br>
	 * The server will no longer accept new client connections, all clients will be forcefully
	 * disconnected and the local port is released.
	 *
	 * @throws P4JServerException if the server socket is already closed
	 */
	@Override
	public synchronized void close() {
		if (ServerStatus.CLOSED.equals(this.serverStatus) || ServerStatus.UNINITIALIZED.equals(this.serverStatus)) {
			throw new P4JServerException("Cannot close not started server socket.");
		}

		try {
			this.serverStatus = ServerStatus.CLOSING;
			this.thread.interrupt();
			this.serverSocketChannel.close();
			this.serverStatus = ServerStatus.CLOSED;

			this.dispatchEvent(new ServerClosedEvent(this));
		} catch (final IOException e) {
			throw new P4JServerException(e);
		}
	}

	/**
	 * Sets the server socket in client refuse mode.<br>
	 * The server will refuse all future incoming client connections but keep current connections alive.
	 *
	 * @throws P4JServerException if the server socket is closed.
	 */
	public void setRefusing() {
		if (ServerStatus.CLOSED.equals(this.serverStatus)) {
			throw new P4JServerException("Cannot set closed server socket in client refuse mode.");
		}

		this.serverStatus = ServerStatus.REFUSING;
	}

	public void registerPacket(final Class<?> p, final int id) {
		this.packets.register(p, id);
	}

	public void dispatchEvent(final P4JEvent event) {
		if (this.eventManager == null) {
			return;
		}

		this.eventManager.dispatch(event, this);
	}

	// ----- thread delegated methods

	public final void join() throws InterruptedException {
		this.thread.join();
	}

	public final void join(final long millis, final int nanos) throws InterruptedException {
		this.thread.join(millis, nanos);
	}

	public final void join(final long millis) throws InterruptedException {
		this.thread.join(millis);
	}

	// ----- thread delegated methods

	public State getState() {
		return this.thread.getState();
	}

	public final boolean isAlive() {
		return this.thread.isAlive();
	}

	public ServerStatus getServerStatus() {
		return this.serverStatus;
	}

	public InetSocketAddress getLocalInetSocketAddress() {
		return this.localInetSocketAddress;
	}

	public Collection<ServerClient> getConnectedClients() {
		return this.clientManager.getAllClients();
	}

	/**
	 * @return the local port bound to the server or -1 if the server is closed
	 */
	public int getPort() {
		return this.serverSocketChannel != null && this.serverSocketChannel.socket() != null
				? this.serverSocketChannel.socket().getLocalPort()
				: -1;
	}

	public CodecManager getCodec() {
		return this.codec;
	}

	public EncryptionManager getEncryption() {
		return this.encryption;
	}

	public CompressionManager getCompression() {
		return this.compression;
	}

	public PacketManager getPackets() {
		return this.packets;
	}

	public ClientManager getClientManager() {
		return this.clientManager;
	}

	public EventManager getEventManager() {
		return this.eventManager;
	}

	public void setCodec(final CodecManager codec) {
		this.codec = codec;
	}

	public void setEncryption(final EncryptionManager encryption) {
		this.encryption = encryption;
	}

	public void setCompression(final CompressionManager compression) {
		this.compression = compression;
	}

	public void setPackets(final PacketManager packets) {
		this.packets = packets;
	}

	public void setClientManager(final ClientManager clientManager) {
		this.clientManager = clientManager;
	}

	public void setEventManager(final EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JServerInstance.super.getEndPoint();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "#" + this.hashCode() + "@{local=" + this.localInetSocketAddress + ", status="
				+ this.serverStatus + ", thread=" + super.toString() + "}";
	}

}
