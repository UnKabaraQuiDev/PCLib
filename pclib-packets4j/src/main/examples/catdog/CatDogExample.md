# CatDog example

- - -

*This example is dumb, but it covers everything useful, except overriden ServerClients*

- - -

Create a Packet:
```java
// This class is used to manage the communication between the Server → Client
// It describes how to handle the received String[] and what value to send
public static class S2C_CatDogPacket implements S2CPacket<Object[]> {

	// Gets called when a Client receives this packet from the connected server
	public void clientRead(P4JClient client, Object[] input) {
		GlobalLogger.info("Question received: ");
		GlobalLogger.info(Arrays.toString(input));
		Random r = new Random();
		int choiceIndex = r.nextInt(input.length);

		// We can cast to a String because we're sure serverWrite() returns String[]
		client.write(new C2S_CatDogPacket((String) input[choiceIndex]));
	}

	// Gets called when using ServerClient.write(new S2C_CatDogPacket())
	// Returns the value to be sent
	public Object[] serverWrite(ServerClient client) {
		GlobalLogger.info("Asked to client");
		return new String[] { "Dog", "or", "Cat" };
	}
}

// This class is used to manage the communication between the Client → Server
// It describes how to handle the received String and what value to send
public static class C2S_CatDogPacket implements C2SPacket<String> {
	String choice;

	// A constructor with no argument is needed or a PacketInstanceException will be thrown
	public C2S_CatDogPacket() {
	}

	public C2S_CatDogPacket(String choice) {
		GlobalLogger.info("Choice prepared: " + choice);
		this.choice = choice;
	}

	// Gets called when using P4JClient.write(new C2S_CatDogPacket())
	// Returns the value to be sent
	public String clientWrite(P4JClient client) {
		GlobalLogger.info("Responding to server: " + choice);
		return this.choice;
	}

	// Gets called when a Server receives this packet from a connected Client
	public void serverRead(ServerClient sclient, String obj) {
		GlobalLogger.info("Client answered: " + obj);
	}
}
```
The `S2CPacket` and `C2SPacket` interfaces could be implementing the same Object.

Create a Server:
```java
CodecManager serverCodec = CodecManager.base();
serverCodec.register(new ArrayEncoder(), new ArrayDecoder(), (short) 11);
EncryptionManager serverEncryption = EncryptionManager.raw();
CompressionManager serverCompression = CompressionManager.raw();
server = new P4JServer(serverCodec, serverEncryption, serverCompression);
server.setEventQueueConsumer(new AsyncEventQueueConsumer());

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
// Starts the server thread
server.setAccepting();
GlobalLogger.info("Server listening and accepting clients");
```

Create a Client:
```java
CodecManager clientCodec = CodecManager.base();
clientCodec.register(new ArrayEncoder(), new ArrayDecoder(), (short) 11);
EncryptionManager clientEncryption = EncryptionManager.raw();
CompressionManager clientCompression = CompressionManager.raw();
client = new P4JClient(clientCodec, clientEncryption, clientCompression);
client.setEventQueueConsumer(new AsyncEventQueueConsumer());

client.getEventManager().register(new ClientEventListener());

// Same as the Server
client.getPackets().register(C2S_CatDogPacket.class, 1);
client.registerPacket(S2C_CatDogPacket.class, 2);

// Bind without any argument takes a free port, a specific port can be passed as argument
client.bind();
GlobalLogger.info("Client bound to port: " + client.getPort());
```

Create EventListeners:
```java
public class ServerEventListener implements EventListener {
	
	@EventHandler
	public void onClientConnect(ClientConnectedEvent event) {
		GlobalLogger.info("Server ClientConnectedEvent: " + ((ClientConnectedEvent) event).getClient());
		CatDogExample.sendChoiceRequest((ServerClient) ((ClientConnectedEvent) event).getClient()); // See "Send Packets"
	}

	@EventHandler
	public void onClientWrite(S2CWritePacketEvent event) {
		if (((S2CWritePacketEvent) event).hasFailed()) {
			GlobalLogger.info("Server ClientWritePacketEvent failed: " + ((S2CWritePacketEvent) event).getException());
		} else {
			GlobalLogger.info("Server ClientWritePacketEvent: " + ((S2CWritePacketEvent) event).getPacket());
		}
	}

	@EventHandler
	public void onClientRead(S2CReadPacketEvent event) {
		if (((S2CReadPacketEvent) event).hasFailed()) {
			GlobalLogger.info("Server ClientReadPacketEvent failed: " + ((S2CReadPacketEvent) event).getException());
		} else {
			GlobalLogger.info("Server ClientReadPacketEvent: " + ((S2CReadPacketEvent) event).getPacket());
		}
	}
	
}
```
```java
public class ClientEventListener implements EventListener {
	
	@EventHandler
	public void onClientConnect(ClientConnectedEvent event) {
		GlobalLogger.info("Client ClientConnectedEvent: " + ((ClientConnectedEvent) event).getClient());
	}

	@EventHandler
	public void onClientWrite(S2CWritePacketEvent event) {
		if (((S2CWritePacketEvent) event).hasFailed()) {
			GlobalLogger.info("Client WritePacketEvent failed: " + ((S2CWritePacketEvent) event).getException());
		} else {
			GlobalLogger.info("Client WritePacketEvent: " + ((S2CWritePacketEvent) event).getPacket());
		}
	}

	@EventHandler
	public void onClientRead(S2CReadPacketEvent event) {
		if (((S2CReadPacketEvent) event).hasFailed()) {
			GlobalLogger.info("Client ReadPacketEvent failed: " + ((S2CReadPacketEvent) event).getException());
		} else {
			GlobalLogger.info("Client ReadPacketEvent: " + ((S2CReadPacketEvent) event).getPacket());
		}
	}
	
}
```

Connect the Client:
```java
// Connect to the server
client.connect(server.getLocalInetSocketAddress());
GlobalLogger.info("Client connected");
```

Send Packets:
```java
// This function gets called when a new client connects
public static void sendChoiceRequest(ServerClient client) {
	GlobalLogger.info("Client connected to server");

	// Send a packet to the newly connected client
	GlobalLogger.info("Packet sent to client: " + client.write(new S2C_CatDogPacket()));

	// OR

	// Broadcast a packet to all clients
	// server.broadcast(new S2C_CatDogPacket());
}
```

In this example, the server-client packet exchange should look like this:
| ORDER | DIR | TYPE | OBJECT | VALUE | FUNCTION |
|------|-----|------|--------|-------|-----------|
| 1. | send | S2C | String[] | input → {"Cat", "or", "Dog"} | serverWrite(ServerClient) → String[] | 
| 2. | read | S2C | String[] | input						| clientRead(P4JClient, String[]) | 
| 3. | send | C2S | String   | choice → input[random]	   | clientWrite(P4JClient) → String | 
| 4. | read | C2S | String   | choice					   | serverRead(ServerClient, String) | 

And the System.out output (for a single client):
```
// server
(INFO)[main] Server bound to port: 8090             // <- bind
(INFO)[main] Server listening and accepting clients // <- setAccepting

// client
(INFO)[main] Client bound to port: 47249                                         // <- bind
(INFO)[main] Client ClientConnectedEvent: Thread[P4JClient@0.0.0.0:47249,5,main] // <- connect
(INFO)[main] Client connected

// server
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Server ClientConnectedEvent: lu.pcy113.p4j.socket.server.ServerClient@c821a32
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Client connected to server // -> sendChoiceRequest
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Asked to client            // <- serverWrite
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Server ClientWritePacketEvent: catdog.S2C_CatDogPacket@2fece981

// client
(INFO)[P4JClient@0.0.0.0:47249] Client ReadPacketEvent: catdog.S2C_CatDogPacket@5a2400f4

// server
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Packet sent to client: true // the packet was sent successfully
(INFO)[P4JClient@0.0.0.0:47249] Question received:                 // <- clientRead
(INFO)[P4JClient@0.0.0.0:47249] [Dog, or, Cat]                     // <- clientRead
(INFO)[P4JClient@0.0.0.0:47249] Choice prepared: or	               // <- C2S_CatDogPacket constructor
(INFO)[P4JClient@0.0.0.0:47249] Responding to server: or           // <- clientWrite
(INFO)[P4JServer@0:0:0:0:0:0:0:0:8090] Client answered: or	       // <- serverRead

// closing the server & client
(INFO)[main] Client: false, TERMINATED and CLOSED
(INFO)[main] Server: false, TERMINATED and CLOSED
```	

Closing the Server & Client:
```java
// We close the client before the server
client.close();
client.join();

server.close();
server.join();

GlobalLogger.info("Client: "+client.isAlive()+", "+client.getState()+" and "+client.getClientStatus());
GlobalLogger.info("Server: "+server.isAlive()+", "+server.getState()+" and "+server.getServerStatus());
```
