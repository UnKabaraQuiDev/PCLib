import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.p4j.compress.CompressionManager;
import lu.kbra.p4j.crypto.EncryptionManager;
import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientDisconnectedEvent;
import lu.kbra.p4j.events.server.ServerClosedEvent;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.P4JServer;
import lu.kbra.pclib.listener.EventHandler;
import lu.kbra.pclib.listener.EventListener;

public class P4JClientEventReconnectMain {

	private P4JServer server;
	private P4JClient client;
	private InetSocketAddress serverAddress;

	public static void main(final String[] args) throws InterruptedException, IOException {
		new P4JClientEventReconnectMain().run();
	}

	public void run() throws InterruptedException, IOException {
		this.setUp();
		this.testClientReconnection();
		this.tearDown();
	}

	public class ClientEventListener implements EventListener {

		@EventHandler
		public void clientDisconnected(final ClientDisconnectedEvent event) {
			System.out.println("[EVENT] [CLIENT] Client disconnected");
		}

		@EventHandler
		public void clientConnected(final ClientConnectedEvent event) {
			System.out.println("[EVENT] [CLIENT] Client connected confirmed");
		}

	}

	public class ServerEventListener implements EventListener {

		@EventHandler
		public void serverClosed(final ServerClosedEvent event) {
			System.out.println("[EVENT] [SERVER] Server closed");
		}

		@EventHandler
		public void clientDisconnected(final ClientDisconnectedEvent event) {
			System.out.println("[EVENT] [SERVER] Client disconnected");
		}

		@EventHandler
		public void clientConnected(final ClientConnectedEvent event) {
			System.out.println("[EVENT] [SERVER] Client connected confirmed");
		}

	}

	@Before
	public void setUp() throws IOException {
		// Start a simple server to accept connections
		this.server = new P4JServer(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
		// server.setEventManager(new AsyncEventManager(1));
		this.server.getEventManager().register(new ServerEventListener());
		this.serverAddress = new InetSocketAddress(0);
		this.server.bind(this.serverAddress);
		this.serverAddress = this.server.getLocalInetSocketAddress();
		this.server.setAccepting();

		// Create and connect client
		this.client = new P4JClient(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
		// client.setEventManager(new AsyncEventManager(1));
		this.client.getEventManager().register(new ClientEventListener());
		this.client.bind();
		this.client.connect(this.serverAddress);
	}

	@After
	public void tearDown() throws InterruptedException {
		this.client.disconnect();
		System.out.println("Disconnected client, waiting");
		this.client.join();
		System.out.println("Client thread shut down");
		this.server.close();
		System.out.println("Closed server, waiting");
		this.server.join();
		System.out.println("Server thread shut down");
		// server.stop();
	}

	@Test
	public void testClientReconnection() throws InterruptedException, IOException {
		Assert.assertTrue("Client should be initially connected", this.client.isConnected());

		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(() -> System.out.println(this.client.isConnected() ? this.client.testConnection() + "" : "not connected"),
				0,
				500,
				TimeUnit.MILLISECONDS);

		Thread.sleep(2000);

		// Simulate server disconnection
		this.server.close();
		System.out.println("server closed");
		this.server.join();
		Thread.sleep(2000); // Allow time for client to detect disconnect

		// assertFalse("Client should detect disconnection", client.isConnected());

		this.server.bind(this.serverAddress);
		this.server.setAccepting();
		System.out.println("server restarted");
		Thread.sleep(200);

		this.client.bind();
		this.client.connect(this.serverAddress);

		// wait for threads started in events
		Thread.sleep(1500);

		Assert.assertTrue("Client should reconnect", this.client.isConnected());

		exec.shutdownNow();
	}

}
