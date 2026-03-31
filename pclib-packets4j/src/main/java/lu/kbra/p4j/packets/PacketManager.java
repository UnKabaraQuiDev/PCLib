package lu.kbra.p4j.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import lu.kbra.p4j.packets.c2s.C2SPacket;
import lu.kbra.p4j.packets.s2c.S2CPacket;
import lu.kbra.p4j.socket.P4JInstance;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.P4JServer;

public class PacketManager {

	private final P4JInstance type;

	public PacketManager(final P4JInstance instance) {
		this.type = instance;
	}

	private final HashMap<Integer, Class<Packet>> inPackets = new HashMap<>();
	private final HashMap<String, Integer> outPackets = new HashMap<>();

	public void register(final Class<?> p, final int id) {
		if (this.type instanceof P4JServer) {
			if (C2SPacket.class.isAssignableFrom(p)) {
				this.inPackets.put(id, (Class<Packet>) p);
			}
			if (S2CPacket.class.isAssignableFrom(p)) {
				this.outPackets.put(p.getName(), id);
			}
		} else if (this.type instanceof P4JClient) {
			if (S2CPacket.class.isAssignableFrom(p)) {
				this.inPackets.put(id, (Class<Packet>) p);
			}
			if (C2SPacket.class.isAssignableFrom(p)) {
				this.outPackets.put(p.getName(), id);
			}
		}
	}

	/**
	 * Returns the id of the packet with the given class<br>
	 * Outgoing packet
	 */
	public int getId(final Class<?> p) {
		if (!this.outPackets.containsKey(p.getName())) {
			throw new UnknownPacketException("Packet: " + p.getName() + "; not registered in PacketManager.");
		}
		return this.outPackets.get(p.getName());
	}

	/**
	 * Returns the class of the packet with the given id<br>
	 * Incoming packet
	 */
	public Class<Packet> getClass(final int id) {
		if (!this.inPackets.containsKey(id)) {
			throw new UnknownPacketException("Packet with id: " + id + "; not registered in PacketManager");
		}
		return this.inPackets.get(id);
	}

	public Packet packetInstance(final int id) throws UnknownPacketException, PacketInstanceException {
		if (!this.inPackets.containsKey(id)) {
			throw new UnknownPacketException("Packet with id: " + id + "; not registered in PacketManager");
		}

		final Class<Packet> pair = this.inPackets.get(id);
		try {
			return pair.getConstructor().newInstance();
		} catch (final NoSuchMethodException e) {
			throw new PacketInstanceException("0-arg constructor for Packet " + pair.getName() + ", not found.");
		} catch (final InstantiationException e) {
			throw new PacketInstanceException(e, "Packet " + pair.getName() + ", cannot be abstract and must have a 0-arg constructor.");
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new PacketInstanceException(e, "Exception occured during initialization");
		}
	}

	public Packet packetInstance(final Class<Packet> cp) throws UnknownPacketException, PacketInstanceException {
		return this.packetInstance(cp.getName());
	}

	public Packet packetInstance(final String cp) throws UnknownPacketException, PacketInstanceException {
		if (!this.outPackets.containsKey(cp)) {
			throw new UnknownPacketException("Packet with name: " + cp + "; not registered in PacketManager");
		}

		return this.packetInstance(this.outPackets.get(cp));
	}

	@Override
	public String toString() {
		return "in: " + this.inPackets + "\nout: " + this.outPackets;
	}

}
