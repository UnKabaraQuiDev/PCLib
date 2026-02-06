package lu.pcy113.p4j.socket.client;

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

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.compress.CompressionManager;
import lu.pcy113.p4j.crypto.EncryptionManager;
import lu.pcy113.p4j.events.P4JEvent;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientDisconnectedEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PreReadPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PreWritePacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.pcy113.p4j.exceptions.P4JClientException;
import lu.pcy113.p4j.exceptions.P4JMaxPacketSizeExceeded;
import lu.pcy113.p4j.exceptions.PacketHandlingException;
import lu.pcy113.p4j.exceptions.PacketWritingException;
import lu.pcy113.p4j.packets.HeartbeatPacket;
import lu.pcy113.p4j.packets.PacketManager;
import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.P4JInstance.P4JClientInstance;
import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.listener.EventDispatcher;
import lu.pcy113.pclib.listener.EventManager;
import lu.pcy113.pclib.listener.SyncEventManager;

/**
 * This class represents the client-side Client connecting to the server.
 * 
 * @author pcy113
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

	private Function<Runnable, Thread> threadFactory = Thread::new;

	private BiFunction<P4JClient, InetSocketAddress, ClientServer> clientServerSupplier = (client, remoteInetSocketAddress) -> new ClientServer(remoteInetSocketAddress);

	/**
	 * 
	 * @param CodecManager       the client codec manager
	 * @param EntryptionManager  the client encryption manager
	 * @param CompressionManager the client compression manager
	 */
	public P4JClient(CodecManager cm, EncryptionManager em, CompressionManager com) {
		this.codec = cm;
		this.encryption = em;
		this.compression = com;

		this.packets.register(HeartbeatPacket.class, 0x00);

		MAX_PACKET_SIZE = PCUtils.parseInteger(System.getProperty("P4J_maxPacketSize"), MAX_PACKET_SIZE);
	}

	/**
	 * Bind to a random available port on the local machine.
	 * 
	 * @throws UnknownHostException
	 * @throws P4JClientException
	 */
	public synchronized void bind() throws UnknownHostException {
		bind(0);
	}

	/**
	 * Bind to the specified port on the local machine. If the port is 0, a random
	 * available port is chosen.
	 * 
	 * @param int the port to bind to
	 * @throws P4JClientException
	 */
	public synchronized void bind(int port) throws UnknownHostException {
		bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port));
	}

	/**
	 * Bind to the specified port on the local machine. If the port is 0, a random
	 * available port is chosen.
	 * 
	 * @param InetSocketAddress the local address to bind to
	 * @throws IOException if the {@link Socket} cannot be created or bound
	 */
	public synchronized void bind(InetSocketAddress isa) {
		try {
			clientSocket = SocketFactory.getDefault().createSocket();
			clientSocket.setKeepAlive(true);
			clientSocket.bind(isa);
			clientStatus = ClientStatus.BOUND;
		} catch (IOException e) {
			throw new P4JClientException(e);
		}

		this.localInetSocketAddress = new InetSocketAddress(clientSocket.getInetAddress(), clientSocket.getLocalPort());

		this.thread = threadFactory.apply(this);
		this.thread.setName("P4JClient@" + localInetSocketAddress.getHostString() + ":" + localInetSocketAddress.getPort());
	}

	/**
	 * Connect to the specified address and port on the remote machine.
	 * 
	 * @param remote the remote address
	 * @param port   the remote port
	 * @throws IOException if the {@link Socket} cannot be connected
	 */
	public synchronized void connect(InetAddress remote, int port) {
		if (!clientStatus.equals(ClientStatus.BOUND)) {
			throw new P4JClientException("Client not bound");
		}
		try {
			clientSocket.connect(new InetSocketAddress(remote, port), connectionTimeout);
			clientSocket.setSoTimeout(200); // ms
			this.inputStream = clientSocket.getInputStream();
			this.outputStream = clientSocket.getOutputStream();

			clientStatus = ClientStatus.LISTENING;

			remoteInetSocketAddress = new InetSocketAddress(clientSocket.getInetAddress(), clientSocket.getPort());

			clientServer = clientServerSupplier.apply(this, remoteInetSocketAddress);

			if (!this.thread.isAlive()) {
				this.thread.start();
			}

			dispatchEvent(new ClientConnectedEvent(P4JEndPoint.CLIENT, this, clientServer));
		} catch (SocketTimeoutException e) {
			throw new P4JClientException("Connection timed out", e);
		} catch (ConnectException e) {
			throw new P4JClientException("Connection refused", e);
		} catch (SocketException e) {
			throw new P4JClientException(e);
		} catch (IOException e) {
			throw new P4JClientException(e);
		} catch (IllegalStateException e) {
			close();
			throw new P4JClientException(e);
		}
	}

	/**
	 * Connect to the specified address and port on the remote machine.
	 * 
	 * @see {@link #connect(InetAddress, int)}
	 */
	public synchronized void connect(InetSocketAddress isa) {
		this.connect(isa.getAddress(), isa.getPort());
	}

	@Override
	public void run() {
		while (clientStatus.equals(ClientStatus.LISTENING)) {
			read();
		}
		// clientStatus = ClientStatus.CLOSED;
	}

	protected void read() {
		try {
			final byte[] cc;

			synchronized (inputStream) {
				final byte[] bb = new byte[4];
				final int bytesRead = inputStream.read(bb);
				if (bytesRead == -1) {
					dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, clientServer, this));
					close();
					return;
				}
				if (bytesRead != 4) {
					return;
				}

				final int length = PCUtils.byteToInt(bb);

				if (length > MAX_PACKET_SIZE) {
					throw new P4JClientException(new P4JMaxPacketSizeExceeded(length));
				}

				cc = new byte[length];
				if (inputStream.read(cc) != length) {
					return;
				}
			}

			final ByteBuffer content = ByteBuffer.wrap(cc);
			final int id = content.getInt();

			read_handleRawPacket(id, content);
		} catch (NotYetConnectedException e) {
			throw new P4JClientException(e);
		} catch (ClosedByInterruptException e) {
			Thread.interrupted(); // clear interrupt flag
			// ignore because triggered in #close()
		} catch (ClosedChannelException e) {
			// ignore because triggered in #close()
		} catch (SocketException e) {
			disconnect();
			if (clientStatus.equals(ClientStatus.LISTENING)) {
				throw new P4JClientException(e);
			}
		} catch (SocketTimeoutException e) {
			// ignore
		} catch (OutOfMemoryError e) {
			throw new P4JClientException(new P4JMaxPacketSizeExceeded(e));
		} catch (IOException e) {
			disconnect();
			if (clientStatus.equals(ClientStatus.LISTENING)) {
				throw new P4JClientException(e);
			}
		}
	}

	protected void read_handleRawPacket(int id, ByteBuffer content) {
		try {
			content = compression.decompress(content);
			content = encryption.decrypt(content);
			Object obj = codec.decode(content);

			S2CPacket packet = (S2CPacket) packets.packetInstance(id);

			dispatchEvent(new PreReadPacketEvent(P4JEndPoint.CLIENT, this, packet, content));

			try {
				packet.clientRead(this, obj);

				dispatchEvent(new ReadSuccessPacketEvent(P4JEndPoint.CLIENT, this, packet, content));
			} catch (Exception e) {
				dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, content));
				throw new P4JClientException(e);
			}
		} catch (Exception e) {
			dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.CLIENT, this, new PacketHandlingException(id, e), null, content));
			throw new P4JClientException(e);
		}
	}

	public boolean testConnection() {
		System.out.println(write(new HeartbeatPacket()));
		try {
			if (!write(new HeartbeatPacket())) {
				close();
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean write(C2SPacket packet) {
		try {
			Object obj = packet.clientWrite(this);
			ByteBuffer content = codec.encode(obj);
			content = encryption.encrypt(content);
			content = compression.compress(content);

			if (content.remaining() + 3 * 4 > MAX_PACKET_SIZE) {
				throw new P4JClientException(new P4JMaxPacketSizeExceeded(content.remaining() + 3 * 4));
			}

			ByteBuffer bb = ByteBuffer.allocate(4 + 4 + content.capacity());
			bb.putInt(content.limit() + 4); // Add id length
			bb.putInt(packets.getId(packet.getClass()));
			bb.put(content);
			bb.flip();

			dispatchEvent(new PreWritePacketEvent(P4JEndPoint.CLIENT, this, packet, bb));

			try {
				synchronized (outputStream) {
					if (bb.hasArray()) {
						outputStream.write(bb.array());
					} else {
						outputStream.write(PCUtils.byteBufferToArray(bb));
					}

					outputStream.flush();
				}

				dispatchEvent(new WriteSuccessPacketEvent(P4JEndPoint.CLIENT, this, packet, bb));

				return true;
			} catch (ClosedChannelException e) {
				dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, e, clientServer, this));
				close();
				return false;
			} catch (SocketException e) {
				disconnect();
				return false;
			} catch (Exception e) {
				dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, bb));
				throw new P4JClientException(e);
			}

		} catch (OutOfMemoryError e) {
			dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT, this, new PacketWritingException(packet, new P4JMaxPacketSizeExceeded(e)), packet, null));
			throw new P4JClientException(new PacketWritingException(packet, new P4JMaxPacketSizeExceeded(e)));
		} catch (Exception e) {
			dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.CLIENT, this, e, packet, null));
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
		close();
		dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.CLIENT, clientServer, this));
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
		if (!clientStatus.equals(ClientStatus.LISTENING)) {
			clientStatus = ClientStatus.CLOSED;
			return;
		}

		try {
			clientStatus = ClientStatus.CLOSING;
			this.thread.interrupt();
			if (clientSocket != null) {
				clientSocket.close();
			}
			clientStatus = ClientStatus.CLOSED;

			clientSocket = null;
			clientServer = null;
			inputStream = null;
			outputStream = null;
		} catch (Exception e) {
			throw new P4JClientException(e);
		}
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
		this.thread.join();
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

	public ClientStatus getClientStatus() {
		return clientStatus;
	}

	public InetSocketAddress getLocalInetSocketAddress() {
		return localInetSocketAddress;
	}

	public InetSocketAddress getRemoteInetSocketAddress() {
		return remoteInetSocketAddress;
	}

	public ClientServer getClientServer() {
		return clientServer;
	}

	/**
	 * @return the current port the client is connected to or -1 if it isn't bound
	 */
	public int getPort() {
		return (clientSocket != null ? clientSocket.getLocalPort() : -1);
	}

	public boolean isBound() {
		return clientSocket.isBound();
	}

	public boolean isConnected() {
		return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
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

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public BiFunction<P4JClient, InetSocketAddress, ClientServer> getClientServerSupplier() {
		return clientServerSupplier;
	}

	public void setClientServerSupplier(BiFunction<P4JClient, InetSocketAddress, ClientServer> clientServerSupplier) {
		this.clientServerSupplier = clientServerSupplier;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JClientInstance.super.getEndPoint();
	}

}