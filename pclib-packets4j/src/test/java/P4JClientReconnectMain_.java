import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.p4j.compress.CompressionManager;
import lu.kbra.p4j.crypto.EncryptionManager;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.P4JServer;

public class P4JClientReconnectMain_ {

	private P4JServer server;
	private P4JClient client;
	private InetSocketAddress serverAddress;

	public static void main(final String[] args) throws InterruptedException, IOException {
		new P4JClientReconnectMain_().run();
	}

	public void run() throws InterruptedException, IOException {
		this.setUp();
		this.testClientReconnection();
		this.tearDown();
	}

	@Before
	public void setUp() throws IOException {
		// Start a simple server to accept connections
		this.server = new P4JServer(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
		this.serverAddress = new InetSocketAddress(0);
		this.server.bind(this.serverAddress);
		this.serverAddress = this.server.getLocalInetSocketAddress();
		this.server.setAccepting();

		// Create and connect client
		this.client = new P4JClient(CodecManager.base(), EncryptionManager.raw(), CompressionManager.raw());
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

		System.out.println(this.client.testConnection());

		// Simulate server disconnection
		this.server.close();
		System.out.println("server closed");
		this.server.join();
		Thread.sleep(5000); // Allow time for client to detect disconnect

		System.out.println(this.client.testConnection());
		Thread.sleep(2000);
		System.out.println(this.client.testConnection());
		Thread.sleep(2000);
		System.out.println(this.client.testConnection());
		Thread.sleep(2000);

		Assert.assertFalse("Client should detect disconnection", this.client.isConnected());

		// Restart server to test reconnection
		// server = new P4JServer(CodecManager.base(), EncryptionManager.raw(),
		// CompressionManager.raw());
		this.server.bind(this.serverAddress);
		this.server.setAccepting();
		System.out.println("server restarted");
		Thread.sleep(200);

		this.client.bind();
		this.client.connect(this.serverAddress);

		// Wait for client to reconnect
		Thread.sleep(5000); // Allow time for reconnection attempts

		// assertTrue("Client should reconnect automatically", client.isConnected());
	}

}
