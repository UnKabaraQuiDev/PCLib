import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.ServerClient;
import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.datastructure.pair.Pairs;
import lu.pcy113.pclib.logger.GlobalLogger;

public class PingPongPacket implements C2SPacket<Pair<Long, String>>, S2CPacket<Pair<Long, String>> {

	private long current;
	private String reason;

	public PingPongPacket() {
	}

	public PingPongPacket(long l, String s) {
		current = l;
		reason = s;
	}

	public PingPongPacket(Pair<Long, String> pair) {
		current = pair.getKey();
		reason = pair.getValue();
	}

	@Override
	public Pair<Long, String> clientWrite(P4JClient client) {
		long x = System.currentTimeMillis();
		return Pairs.readOnly(x, "ping");
	}

	@Override
	public void serverRead(ServerClient sclient, Pair<Long, String> obj) {
		GlobalLogger.info("server read");
		GlobalLogger.info("server packet sent: " + sclient.write(new PingPongPacket(obj)));
	}

	@Override
	public void clientRead(P4JClient client, Pair<Long, String> obj) {
		GlobalLogger.info("client read: " + obj);
	}

	@Override
	public Pair<Long, String> serverWrite(ServerClient client) {
		long x = System.currentTimeMillis() - current;
		return Pairs.readOnly(x, reason);
	}

}
