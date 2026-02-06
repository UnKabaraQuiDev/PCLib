package lu.pcy113.p4j.socket.server;

import java.io.Closeable;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientDisconnectedEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PreReadPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PreWritePacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.pcy113.p4j.exceptions.P4JClientException;
import lu.pcy113.p4j.exceptions.P4JMaxPacketSizeExceeded;
import lu.pcy113.p4j.exceptions.P4JServerClientException;
import lu.pcy113.p4j.exceptions.PacketHandlingException;
import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.P4JInstance.P4JServerClientInstance;

/**
 * Represents a P4JServer's client. This is a wrapper around a
 * {@link SocketChannel} that represents a client connected to the server, on
 * the server-side.
 */
public class ServerClient implements P4JServerClientInstance, Closeable {

	protected ServerClientStatus serverClientStatus = ServerClientStatus.PRE;

	protected UUID uuid;
	protected P4JServer server;

	protected SocketChannel socketChannel;

	protected Consumer<P4JServerClientException> exceptionConsumer = (P4JServerClientException e) -> System.err.println(e.getMessage());

	public ServerClient(SocketChannel sc, P4JServer server) {
		this.socketChannel = sc;
		this.server = server;

		this.uuid = UUID.randomUUID();

		this.serverClientStatus = ServerClientStatus.LISTENING;
	}

	public void read() {
		try {
			ByteBuffer content;

			synchronized (socketChannel) {
				final ByteBuffer bb = ByteBuffer.allocate(4);
				final int bytesRead = socketChannel.read(bb);
				if (bytesRead == -1) {
					server.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.SERVER_CLIENT, server, this));
					close();
					return;
				}

				if (bytesRead != 4)
					return;

				bb.flip();
				final int length = bb.getInt();
				bb.clear();

				if (length > P4JServer.MAX_PACKET_SIZE) {
					handleException(new P4JServerClientException(new P4JMaxPacketSizeExceeded(length)));
					return;
				}

				content = ByteBuffer.allocate(length);
				if (socketChannel.read(content) != length)
					return;
			}

			content.flip();
			final int id = content.getInt();

			read_handleRawPacket(id, content);

			content.clear();
		} catch (ClosedByInterruptException e) {
			// ignore because triggered in #close()
		} catch (ClosedChannelException | SocketException e) {
			server.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.SERVER_CLIENT, e, server, this));
			close();
		} catch (OutOfMemoryError e) {
			handleException(new P4JServerClientException(e));
		} catch (Exception e) {
			handleException(new P4JServerClientException(e));
		}
	}

	protected void read_handleRawPacket(int id, ByteBuffer content) {
		try {
			content = server.getCompression().decompress(content);
			content = server.getEncryption().decrypt(content);
			Object obj = server.getCodec().decode(content);

			C2SPacket packet = (C2SPacket) server.getPackets().packetInstance(id);

			server.dispatchEvent(new PreReadPacketEvent(P4JEndPoint.SERVER_CLIENT, this, packet, content));

			try {
				packet.serverRead(this, obj);

				server.dispatchEvent(new ReadSuccessPacketEvent(P4JEndPoint.SERVER_CLIENT, this, packet, content));
			} catch (Exception e) {
				server.dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.SERVER_CLIENT, this, e, packet, content));
				handleException(new P4JServerClientException(e));
			}

		} catch (Exception e) {
			server.dispatchEvent(new ReadFailedPacketEvent(P4JEndPoint.SERVER_CLIENT, this, new PacketHandlingException(id, e), null, content));
			handleException(new P4JServerClientException(e));
		}
	}

	/**
	 * Writes a packet from the server to the client.
	 * 
	 * @param S2CPacket the packet to write to the client
	 * @return If the packet was written successfully
	 */
	public boolean write(S2CPacket packet) {
		Objects.requireNonNull(packet);

		try {
			ByteBuffer content = server.getCodec().encode(packet.serverWrite(this));
			content = server.getEncryption().encrypt(content);
			content = server.getCompression().compress(content);

			final int id = server.getPackets().getId(packet.getClass());

			final ByteBuffer bb = ByteBuffer.allocate(4 + 4 + content.capacity());
			bb.putInt(content.limit() + 4); // Add id length
			bb.putInt(id);
			bb.put(content);
			bb.flip();

			server.dispatchEvent(new PreWritePacketEvent(P4JEndPoint.SERVER_CLIENT, this, packet, bb));

			try {
				synchronized (socketChannel) {
					final int length = socketChannel.write(bb);
				}

				server.dispatchEvent(new WriteSuccessPacketEvent(P4JEndPoint.SERVER_CLIENT, this, packet, bb));
				return true;
			} catch (Exception e) {
				server.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.SERVER_CLIENT, this, e, packet, content));
				handleException(new P4JServerClientException(e));
				return false;
			}

		} catch (ClosedChannelException e) {
			server.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.SERVER_CLIENT, e, server, this));
			server.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.SERVER_CLIENT, this, e, packet, null));
			return false;
		} catch (Exception e) {
			server.dispatchEvent(new WriteFailedPacketEvent(P4JEndPoint.SERVER_CLIENT, this, e, packet, null));
			handleException(new P4JServerClientException(e));
			return false;
		}
	}

	/**
	 * Handles the given exception in this client instance.<br>
	 * It is strongly encouraged to override this method.
	 * 
	 * @param Exception the exception
	 */
	private void handleException(P4JServerClientException e) {
		if (exceptionConsumer != null) {
			exceptionConsumer.accept(e);
		}
	}

	/**
	 * Disconnects & closes the client socket<br>
	 * And dispatches a {@link ClientDisconnectedEvent}.
	 * 
	 * @see #close()
	 * @throws P4JClientException if the client socket is already closed or isn't
	 *                            started
	 */
	public synchronized void disconnect() {
		close();
		server.dispatchEvent(new ClientDisconnectedEvent(P4JEndPoint.SERVER_CLIENT, server, this));
	}

	/**
	 * Closes the client socket.<br>
	 * Doesn't dispatch a {@link ClientDisconnectedEvent}.
	 * 
	 * @see {@link #disconnect()}
	 * @throws P4JClientException if the client socket is already closed or isn't
	 *                            started
	 */
	@Override
	public synchronized void close() {
		if (serverClientStatus.equals(ServerClientStatus.CLOSED) || serverClientStatus.equals(ServerClientStatus.PRE))
			throw new P4JClientException("Cannot close unstarted client socket.");

		try {
			serverClientStatus = ServerClientStatus.CLOSING;
			socketChannel.close();
			serverClientStatus = ServerClientStatus.CLOSED;

			server.getClientManager().remove(this);
		} catch (Exception e) {
			handleException(new P4JServerClientException(e));
		}
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public P4JServer getServer() {
		return server;
	}

	public UUID getUUID() {
		return uuid;
	}

	public ServerClientStatus getServerClientStatus() {
		return serverClientStatus;
	}

	public Consumer<P4JServerClientException> getExceptionConsumer() {
		return exceptionConsumer;
	}

	public void setExceptionConsumer(Consumer<P4JServerClientException> exceptionConsumer) {
		this.exceptionConsumer = exceptionConsumer;
	}

	@Override
	public final P4JEndPoint getEndPoint() {
		return P4JServerClientInstance.super.getEndPoint();
	}

	@Override
	public String toString() {
		return getClass().getName() + "#" + hashCode() + "@{server=" + server + ", uuid=" + uuid + ", status=" + serverClientStatus + "}";
	}

}