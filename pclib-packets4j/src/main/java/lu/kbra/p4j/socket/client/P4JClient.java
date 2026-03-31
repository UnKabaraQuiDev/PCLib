package lu.kbra.p4j.socket.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.net.SocketFactory;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.p4j.P4JEndPoint;
import lu.kbra.p4j.compress.CompressionManager;
import lu.kbra.p4j.crypto.EncryptionManager;
import lu.kbra.p4j.events.P4JEvent;
import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientDisconnectedEvent;
import lu.kbra.p4j.events.packets.PacketEvent.PreReadPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.PreWritePacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.kbra.p4j.exceptions.P4JClientException;
import lu.kbra.p4j.exceptions.P4JMaxPacketSizeExceeded;
import lu.kbra.p4j.exceptions.PacketHandlingException;
import lu.kbra.p4j.exceptions.PacketWritingException;
import lu.kbra.p4j.packets.HeartbeatPacket;
import lu.kbra.p4j.packets.PacketManager;
import lu.kbra.p4j.packets.c2s.C2SPacket;
import lu.kbra.p4j.packets.s2c.S2CPacket;
import lu.kbra.p4j.socket.P4JInstance.P4JClientInstance;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.listener.EventDispatcher;
import lu.kbra.pclib.listener.EventManager;
import lu.kbra.pclib.listener.SyncEventManager;

/**
 * This class represents the client-side Client connecting to the server.
 *
 * @author kbra
 */
public class P4JClient implements P4JClientInstance, EventDispatcher, Closeable, Runnable {

	public static int MAX_PACKET_SIZE = 2048;

	private ClientStatus clientStatus = ClientStatus.PRE;

	private EventManager eventManager = new SyncEventManager();

	private CodecManager codec;
	private EncryptionManager encryption;
	private CompressionManager compression;
	private PacketManager packets = new PacketManager(this);

	private int connectionTimeout = 5000;
	private InetSocketAddress localInetSocketAddress, remoteInetSocketAddress;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;

	private Thread thread;
	private ClientServer clientServer;

	private final Function<Runnable, Thread> threadFactory = Thread::new;

	private BiFunction<P4JClient, InetSocketAddress, ClientServer> clientServerSupplier = (
			client,
			remoteInetSocketAddress) -> new ClientServer(remoteInetSocketAddress);

	/**
	 *
	 * @param CodecManager       the client codec manager
	 * @param EntryptionManager  the client encryption manager
	 * @param CompressionManager the client compression manager
	 */
	public P4JClient(final CodecManager cm, final EncryptionManager em, final CompressionManager com) {
		this.codec = cm;
		this.encryption = em;
		this.compression = com;

		this.packets.register(HeartbeatPacket.class, 0x00);

		P4JClient.MAX_PACKET_SIZE = PCUtils.parseInteger(System.getProperty("P4J_maxPacketSize"), P4JClient.MAX_PACKET_SIZE);
	}

	/**
	 * Bind to a random available port on the local machine.
	 *
	 * @throws UnknownHostException
	 * @throws P4JClientException
	 */
	public synchronized void bind() throws UnknownHostException {
		this.bind(0);
	}

	/**
	 * Bind to the specified port on the local machine. If the port is 0, a random available port is
	 * chosen.
	 *
	 * @param int the port to bind to
	 * @throws P4JClientException
	 */
	public synchronized void bind(final int port) throws UnknownHostException {
		this.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port));
	}

	/**
	 * Bind to the specified port on the local machine. If the port is 0, a random available port is
	 * chosen.
	 *
	 * @param InetSocketAddress the local address to bind to
	 * @throws IOException if the {@link Socket} cannot be created or bound
	 */
	public synchronized void bind(final InetSocketAddress isa) {
		try {
			this.clientSocket = SocketFactory.getDefault().createSocket();
			this.clientSocket.setKeepAlive(true);
			this.clientSocket.bind(isa);
			this.clientStatus = ClientStatus.BOUND;
		} catch (final IOException e) {
			throw new P4JClientException(e);
		}

		this.localInetSocketAddress = new InetSocketAddress(this.clientSocket.getInetAddress(), this.clientSocket.getLocalPort());

		this.thread = this.threadFactory.apply(this);
		this.thread.setName("P4JClient@" + this.localInetSocketAddress.getHostString() + ":" + this.localInetSocketAddress.getPort());
	}

	/**
	 * Connect to the specified address and port on the remote machine.
	 *
	 * @param remote the remote address
	 * @param port   the remote port
	 * @throws IOException if the {@link Socket} cannot be connected
	 */
	public synchronized void connect(final InetAddress remote, final int port) {
		if (!ClientStatus.BOUND.equals(this.clientStatus)) {
			throw new P4JClientException("Client not bound");
		}
		try {
			this.clientSocket.connect(new InetSocketAddress(remote, port), this.connectionTimeout);
			this.clientSocket.setSoTimeout(200); // ms
			this.inputStream = this.clientSocket.getInputStream();
			this.outputStream = this.clientSocket.getOutputStream();

			this.clientStatus = ClientStatus.LISTENING;

			this.remoteInetSocketAddress = new InetSocketAddress(this.clientSocket.getInetAddress(), this.clientSocket.getPort());

			this.clientServer = this.clientServerSupplier.apply(this, this.remoteInetSocketAddress);

			if (!this.thread.isAlive()) {
				this.thread.start();
			}

			this.dispatchEvent(new ClientConnectedEvent(P4JEndPoint.CLIENT, this, this.clientServer));
		} catch (final SocketTimeoutException e) {
			throw new P4JClientException("Connection timed out", e);
		} catch (final ConnectException e) {
			throw new P4JClientException("Connection refused", e);
		} catch (final IOException e) {
			throw new P4JClientException(e);
		} catch (final IllegalStateException e) {
			this.close();
			throw new P4JClientException(e);
		}
	}

	/**
	 * Connect to the specified address and port on the remote machine.
	 *
	 * @see {@link #connect(InetAddress, int)}
	 */
	public synchronized void connect(final InetSocketAddress isa) {
		this.connect(isa.getAddress(), isa.getPort());
	}

	@Override
	public void run() {
		while (ClientStatus.LISTENING.equals(this.clientStatus)) {
			this.read();
		}
		// clientStatus = ClientStatus.CLOSED;
	}

	protected void read() {
		try {
			final byte[] cc;

			synchronized (this.inputStream) {
				final byte[] bb = new byte[4];
				final int bytesRead = this.inputStream.read(bb);
				if (bytesRead == -1) {
					this.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, this.clientServer, this));
					this.close();
					return;
				}
				if (bytesRead != 4) {
					return;
				}

				final int length = PCUtils.byteToInt(bb);

				if (length > P4JClient.MAX_PACKET_SIZE) {
					throw new P4JClientException(new P4JMaxPacketSizeExceeded(length));
				}

				cc = new byte[length];
				if (this.inputStream.read(cc) != length) {
					return;
				}
			}

			final ByteBuffer content = ByteBuffer.wrap(cc);
			final int id = content.getInt();

			this.read_handleRawPacket(id, content);
		} catch (final NotYetConnectedException e) {
			throw new P4JClientException(e);
		} catch (final ClosedByInterruptException e) {
			Thread.interrupted(); // clear interrupt flag
			// ignore because triggered in #close()
		} catch (final SocketException e) {
			this.disconnect();
			if (ClientStatus.LISTENING.equals(this.clientStatus)) {
				throw new P4JClientException(e);
			}
		} catch (ClosedChannelException | SocketTimeoutException e) {
			// ignore
		} catch (final OutOfMemoryError e) {
			throw new P4JClientException(new P4JMaxPacketSizeExceeded(e));
		} catch (final IOException e) {
			this.disconnect();
			if (ClientStatus.LISTENING.equals(this.clientStatus)) {
				throw new P4JClientException(e);
			}
		}
	}

	protected void read_handleRawPacket(final int id, ByteBuffer content) {
		try {
			content = this.compression.decompress(content);
			content = this.encryption.decrypt(content);
			final Object obj = this.codec.decode(content);

			final S2CPacket packet = (S2CPacket) this.packets.packetInstance(id);

			this.dispatchEvent(new PreReadPacketEvent(P4JEndPoint.CLIENT, this, packet, content));

			try {
				packet.clientRead(this, obj);

				this.dispatchEvent(new ReadSuccessPacketEvent(P4JEndPoint.CLIENT, this, packet, content));
			} catch (final Exception e) {
				this.dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, content));
				throw new P4JClientException(e);
			}
		} catch (final Exception e) {
			this.dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.CLIENT, this, new PacketHandlingException(id, e), null, content));
			throw new P4JClientException(e);
		}
	}

	public boolean testConnection() {
		System.out.println(this.write(new HeartbeatPacket()));
		try {
			if (!this.write(new HeartbeatPacket())) {
				this.close();
				return false;
			}
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean write(final C2SPacket packet) {
		try {
			final Object obj = packet.clientWrite(this);
			ByteBuffer content = this.codec.encode(obj);
			content = this.encryption.encrypt(content);
			content = this.compression.compress(content);

			if (content.remaining() + 3 * 4 > P4JClient.MAX_PACKET_SIZE) {
				throw new P4JClientException(new P4JMaxPacketSizeExceeded(content.remaining() + 3 * 4));
			}

			final ByteBuffer bb = ByteBuffer.allocate(4 + 4 + content.capacity());
			bb.putInt(content.limit() + 4); // Add id length
			bb.putInt(this.packets.getId(packet.getClass()));
			bb.put(content);
			bb.flip();

			this.dispatchEvent(new PreWritePacketEvent(P4JEndPoint.CLIENT, this, packet, bb));

			try {
				synchronized (this.outputStream) {
					if (bb.hasArray()) {
						this.outputStream.write(bb.array());
					} else {
						this.outputStream.write(PCUtils.allByteBufferToArray(bb));
					}

					this.outputStream.flush();
				}

				this.dispatchEvent(new WriteSuccessPacketEvent(P4JEndPoint.CLIENT, this, packet, bb));

				return true;
			} catch (final ClosedChannelException e) {
				this.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, e, this.clientServer, this));
				this.close();
				return false;
			} catch (final SocketException e) {
				this.disconnect();
				return false;
			} catch (final Exception e) {
				this.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, bb));
				throw new P4JClientException(e);
			}

		} catch (final OutOfMemoryError e) {
			this.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT,
					this,
					new PacketWritingException(packet, new P4JMaxPacketSizeExceeded(e)),
					packet,
					null));
			throw new P4JClientException(new PacketWritingException(packet, new P4JMaxPacketSizeExceeded(e)));
		} catch (final Exception e) {
			this.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, null));
			throw new P4JClientException(e);
		}
	}

	/**
	 * Disconnects & closes the client socket<br>
	 * And dispatches a {@link ClientDisconnectedEvent}.
	 *
	 * @see {@link #close()}
	 * @throws P4JClientException if the client isn't started
	 */
	public synchronized void disconnect() {
		this.close();
		this.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, this.clientServer, this));
	}

	/**
	 * Closes the client socket.<br>
	 * The client' socket will be closed and the port will be released.<br>
	 * Doesn't dispatch a {@link ClientDisconnectedEvent}.
	 *
	 * @see {@link #disconnect()}
	 * @throws P4JClientException if the client isn't started
	 */
	@Override
	public synchronized void close() {
		if (!ClientStatus.LISTENING.equals(this.clientStatus)) {
			this.clientStatus = ClientStatus.CLOSED;
			return;
		}

		try {
			this.clientStatus = ClientStatus.CLOSING;
			this.thread.interrupt();
			if (this.clientSocket != null) {
				this.clientSocket.close();
			}
			this.clientStatus = ClientStatus.CLOSED;

			this.clientSocket = null;
			this.clientServer = null;
			this.inputStream = null;
			this.outputStream = null;
		} catch (final Exception e) {
			throw new P4JClientException(e);
		}
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

	public ClientStatus getClientStatus() {
		return this.clientStatus;
	}

	public InetSocketAddress getLocalInetSocketAddress() {
		return this.localInetSocketAddress;
	}

	public InetSocketAddress getRemoteInetSocketAddress() {
		return this.remoteInetSocketAddress;
	}

	public ClientServer getClientServer() {
		return this.clientServer;
	}

	/**
	 * @return the current port the client is connected to or -1 if it isn't bound
	 */
	public int getPort() {
		return this.clientSocket != null ? this.clientSocket.getLocalPort() : -1;
	}

	public boolean isBound() {
		return this.clientSocket.isBound();
	}

	public boolean isConnected() {
		return this.clientSocket != null && this.clientSocket.isConnected() && !this.clientSocket.isClosed();
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

	public void setEventManager(final EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setConnectionTimeout(final int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public BiFunction<P4JClient, InetSocketAddress, ClientServer> getClientServerSupplier() {
		return this.clientServerSupplier;
	}

	public void setClientServerSupplier(final BiFunction<P4JClient, InetSocketAddress, ClientServer> clientServerSupplier) {
		this.clientServerSupplier = clientServerSupplier;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JClientInstance.super.getEndPoint();
	}

}
