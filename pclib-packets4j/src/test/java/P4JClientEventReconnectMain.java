import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.p4j.compress.CompressionManager;
import lu.pcy113.p4j.crypto.EncryptionManager;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientDisconnectedEvent;
import lu.pcy113.p4j.events.server.ServerClosedEvent;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.P4JServer;
import lu.pcy113.pclib.listener.EventHandler;
import lu.pcy113.pclib.listener.EventListener;

public class P4JClientEventReconnectMain {

	private P4JServer server;
	private P4JClient client;
	private InetSocketAddress serverAddress;

	public static void main(String[] args) throws InterruptedException, IOException {
		new P4JClientEventReconnectMain().run();
	}

	public void run() throws InterruptedException, IOException {
		setUp();
		testClientReconnection();
		tearDown();
	}

	public class ClientEventListener implements EventListener {

		@EventHandler
		public void clientDisconnected(ClientDisconnectedEvent event) {
			System.out.println("[EVENT] [CLIENT] Client disconnected");
		}

		@EventHandler
		public void clientConnected(ClientConnectedEvent event) {
			System.out.println("[EVENT] [CLIENT] Client connected confirmed");
		}

	}

	public class ServerEventListener implements EventListener {

		@EventHandler
		public void serverClosed(ServerClosedEvent event) {
			System.out.println("[EVENT] [SERVER] Server closed");
		}

		@EventHandler
		public void clientDisconnected(ClientDisconnectedEvent event) {
			System.out.println("[EVENT] [SERVER] Client disconnected");
		}

		@EventHandler
		public void clientConnected(ClientConnectedEvent event) {
			System.out.println("[EVENT] [SERVER] Client connected confirmed");
		}

	}

	@Before
	public void setUp() throws IOException {
		// Start a simple server to accept connections
		server = new P4JServer(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
		// server.setEventManager(new AsyncEventManager(1));
		server.getEventManager().register(new ServerEventListener());
		serverAddress = new InetSocketAddress(0);
		server.bind(serverAddress);
		serverAddress = server.getLocalInetSocketAddress();
		server.setAccepting();

		// Create and connect client
		client = new P4JClient(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
		// client.setEventManager(new AsyncEventManager(1));
		client.getEventManager().register(new ClientEventListener());
		client.bind();
		client.connect(serverAddress);
	}

	@After
	public void tearDown() throws InterruptedException {
		client.disconnect();
		System.out.println("Disconnected client, waiting");
		client.join();
		System.out.println("Client thread shut down");
		server.close();
		System.out.println("Closed server, waiting");
		server.join();
		System.out.println("Server thread shut down");
		// server.stop();
	}

	@Test
	public void testClientReconnection() throws InterruptedException, IOException {
		assertTrue("Client should be initially connected", client.isConnected());

		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(() -> System.out.println(client.isConnected() ? client.testConnection() + "" : "not connected"), 0, 500, TimeUnit.MILLISECONDS);

		Thread.sleep(2000);

		// Simulate server disconnection
		server.close();
		System.out.println("server closed");
		server.join();
		Thread.sleep(2000); // Allow time for client to detect disconnect

		// assertFalse("Client should detect disconnection", client.isConnected());

		server.bind(serverAddress);
		server.setAccepting();
		System.out.println("server restarted");
		Thread.sleep(200);

		client.bind();
		client.connect(serverAddress);

		// wait for threads started in events
		Thread.sleep(1500);

		assertTrue("Client should reconnect", client.isConnected());

		exec.shutdownNow();
	}

}
