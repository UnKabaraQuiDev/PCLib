import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.jbcodec.decoder.PairDecoder;
import lu.kbra.jbcodec.encoder.PairEncoder;
import lu.kbra.p4j.compress.CompressionManager;
import lu.kbra.p4j.crypto.EncryptionManager;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.P4JServer;
import lu.kbra.pclib.logger.GlobalLogger;

public class PingPongMain {

	@Test
	public void pingpong() {
		try {
			if (!GlobalLogger.isInit()) {
				GlobalLogger.initDefault();
			}

			P4JServer server = new P4JServer(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
			server.getCodec().register(new PairEncoder(), new PairDecoder(), (short) 22);
			server.bind(new InetSocketAddress(11_000));
			server.getPackets().register(PingPongPacket.class, 1);
			server.setAccepting();

			GlobalLogger.info("server done");

			P4JClient client = new P4JClient(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
			client.getCodec().register(new PairEncoder(), new PairDecoder(), (short) 22);
			client.bind();
			client.getPackets().register(PingPongPacket.class, 1);
			client.connect(InetAddress.getLocalHost(), server.getLocalInetSocketAddress().getPort());
			GlobalLogger.info("client addr: " + client.getLocalInetSocketAddress());
			GlobalLogger.info("client remote addr: " + client.getClientServer().getRemoteInetSocketAddress());

			GlobalLogger.info("client done");

			GlobalLogger.info("client packet sent: " + client.write(new PingPongPacket()));

			Thread.sleep(1);
			
			client.disconnect();
			GlobalLogger.info("client closed waiting for thread to end");
			client.join();
			GlobalLogger.info("client thread ended");
			
			server.disconnectAll();
			GlobalLogger.info("server disconnected all clients");
			server.close();
			GlobalLogger.info("server closed waiting for thread to end");
			client.close();
			GlobalLogger.info("server thread ended");
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

}
