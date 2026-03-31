import lu.kbra.p4j.packets.c2s.C2SPacket;
import lu.kbra.p4j.packets.s2c.S2CPacket;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.ServerClient;
import lu.kbra.pclib.datastructure.pair.Pair;
import lu.kbra.pclib.datastructure.pair.Pairs;
import lu.kbra.pclib.logger.GlobalLogger;

public class PingPongPacket implements C2SPacket<Pair<Long, String>>, S2CPacket<Pair<Long, String>> {

	private long current;
	private String reason;

	public PingPongPacket() {
	}

	public PingPongPacket(final long l, final String s) {
		this.current = l;
		this.reason = s;
	}

	public PingPongPacket(final Pair<Long, String> pair) {
		this.current = pair.getKey();
		this.reason = pair.getValue();
	}

	@Override
	public Pair<Long, String> clientWrite(final P4JClient client) {
		final long x = System.currentTimeMillis();
		return Pairs.readOnly(x, "ping");
	}

	@Override
	public void serverRead(final ServerClient sclient, final Pair<Long, String> obj) {
		GlobalLogger.info("server read");
		GlobalLogger.info("server packet sent: " + sclient.write(new PingPongPacket(obj)));
	}

	@Override
	public void clientRead(final P4JClient client, final Pair<Long, String> obj) {
		GlobalLogger.info("client read: " + obj);
	}

	@Override
	public Pair<Long, String> serverWrite(final ServerClient client) {
		final long x = System.currentTimeMillis() - this.current;
		return Pairs.readOnly(x, this.reason);
	}

}
