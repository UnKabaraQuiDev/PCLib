package lu.pcy113.p4j.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.P4JInstance;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.P4JServer;

public class PacketManager {

	private P4JInstance type;

	public PacketManager(P4JInstance instance) {
		type = instance;
	}

	private HashMap<Integer, Class<Packet>> inPackets = new HashMap<>();
	private HashMap<String, Integer> outPackets = new HashMap<>();

	public void register(Class<?> p, int id) {
		if (type instanceof P4JServer) {
			if (C2SPacket.class.isAssignableFrom(p)) {
				inPackets.put(id, (Class<Packet>) p);
			}
			if (S2CPacket.class.isAssignableFrom(p)) {
				outPackets.put(p.getName(), id);
			}
		} else if (type instanceof P4JClient) {
			if (S2CPacket.class.isAssignableFrom(p)) {
				inPackets.put(id, (Class<Packet>) p);
			}
			if (C2SPacket.class.isAssignableFrom(p)) {
				outPackets.put(p.getName(), id);
			}
		}
	}

	/**
	 * Returns the id of the packet with the given class<br>
	 * Outgoing packet
	 */
	public int getId(Class<?> p) {
		if (!outPackets.containsKey(p.getName()))
			throw new UnknownPacketException("Packet: " + p.getName() + "; not registered in PacketManager.");
		return outPackets.get(p.getName());
	}

	/**
	 * Returns the class of the packet with the given id<br>
	 * Incoming packet
	 */
	public Class<Packet> getClass(int id) {
		if (!inPackets.containsKey(id))
			throw new UnknownPacketException("Packet with id: " + id + "; not registered in PacketManager");
		return inPackets.get(id);
	}

	public Packet packetInstance(int id) throws UnknownPacketException, PacketInstanceException {
		if (!inPackets.containsKey(id))
			throw new UnknownPacketException("Packet with id: " + id + "; not registered in PacketManager");

		Class<Packet> pair = inPackets.get(id);
		try {
			return pair.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			throw new PacketInstanceException("0-arg constructor for Packet " + pair.getName() + ", not found.");
		} catch (InstantiationException e) {
			throw new PacketInstanceException(e, "Packet " + pair.getName() + ", cannot be abstract and must have a 0-arg constructor.");
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new PacketInstanceException(e, "Exception occured during initialization");
		}
	}

	public Packet packetInstance(Class<Packet> cp) throws UnknownPacketException, PacketInstanceException {
		return packetInstance(cp.getName());
	}

	public Packet packetInstance(String cp) throws UnknownPacketException, PacketInstanceException {
		if (!outPackets.containsKey(cp))
			throw new UnknownPacketException("Packet with name: " + cp + "; not registered in PacketManager");

		return packetInstance(outPackets.get(cp));
	}

	@Override
	public String toString() {
		return "in: " + inPackets + "\nout: " + outPackets;
	}

}