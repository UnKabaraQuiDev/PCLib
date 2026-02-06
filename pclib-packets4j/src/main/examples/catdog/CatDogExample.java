package catdog;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.ArrayDecoder;
import lu.pcy113.jbcodec.encoder.ArrayEncoder;
import lu.pcy113.p4j.compress.CompressionManager;
import lu.pcy113.p4j.crypto.EncryptionManager;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.P4JServer;
import lu.pcy113.p4j.socket.server.ServerClient;
import lu.pcy113.pclib.logger.GlobalLogger;

public class CatDogExample {

	private static P4JServer server;
	private static P4JClient client;

	public static void main(String[] args) throws IOException, InterruptedException {

		GlobalLogger.init(new File("src/main/examples/test_logs.properties"));

		// CREATE A SERVER

		CodecManager serverCodec = CodecManager.base();
		serverCodec.register(new ArrayEncoder(), new ArrayDecoder(), (short) 11);
		EncryptionManager serverEncryption = EncryptionManager.raw();
		CompressionManager serverCompression = CompressionManager.raw();
		server = new P4JServer(serverCodec, serverEncryption, serverCompression);

		// Attach a listener to handle new connected clients
		server.getEventManager().register(new ServerEventListener());

		// Register incoming and outdoing packets
		// We can't use the same id, because the classes haven't the same argument
		// S2C packet takes a Object[] and C2S packet takes a String
		server.getPackets().register(C2S_CatDogPacket.class, 1);
		// registerPacket() is a shortcut for getPackets().register()
		server.registerPacket(S2C_CatDogPacket.class, 2);

		// Bind to the local port
		server.bind(new InetSocketAddress(8090));
		GlobalLogger.info("Server bound to port: " + server.getPort());

		// Set as listening and accepting clients
		server.setAccepting();
		GlobalLogger.info("Server listening and accepting clients");

		// CREATE A CLIENT

		CodecManager clientCodec = CodecManager.base();
		clientCodec.register(new ArrayEncoder(), new ArrayDecoder(), (short) 11);
		EncryptionManager clientEncryption = EncryptionManager.raw();
		CompressionManager clientCompression = CompressionManager.raw();
		client = new P4JClient(clientCodec, clientEncryption, clientCompression);

		// same as the server
		client.getEventManager().register(new ClientEventListener());

		// Same as the Server
		client.getPackets().register(C2S_CatDogPacket.class, 1);
		client.registerPacket(S2C_CatDogPacket.class, 2);

		// Bind without any argument takes a free port, a specific port can be passed as
		// argument
		client.bind();
		GlobalLogger.info("Client bound to port: " + client.getPort());

		// CONNECT THE CLIENT TO THE SERVER

		client.connect(server.getLocalInetSocketAddress());
		GlobalLogger.info("Client connected");

		// The server will send a packet to the client
		// as soon as the client connects
		// See "Create a Server"

		Thread.sleep(2000);

		client.disconnect();
		client.join();
		server.close();
		server.join();

		GlobalLogger.info("Client: " + client.isAlive() + ", " + client.getState() + " and " + client.getClientStatus());
		GlobalLogger.info("Server: " + server.isAlive() + ", " + server.getState() + " and " + server.getServerStatus());
	}

	// See "Create a Server"
	// This function gets called when a new client connects
	public static void sendChoiceRequest(ServerClient client) {
		GlobalLogger.info("Client connected to server");

		// Send a packet to the newly connected client
		GlobalLogger.info("Packet sent to client: " + client.write(new S2C_CatDogPacket()));

		// OR

		// Broadcast a packet to all clients
		// server.broadcast(new S2C_CatDogPacket());
	}

}
