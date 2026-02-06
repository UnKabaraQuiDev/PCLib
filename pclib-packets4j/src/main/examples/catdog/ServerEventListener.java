package catdog;

import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.kbra.p4j.socket.server.ServerClient;
import lu.kbra.pclib.listener.EventDispatcher;
import lu.kbra.pclib.listener.EventHandler;
import lu.kbra.pclib.listener.EventListener;
import lu.kbra.pclib.listener.EventManager;
import lu.kbra.pclib.logger.GlobalLogger;

public class ServerEventListener implements EventListener {

	@EventHandler
	public void onClientConnect(ClientConnectedEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Server ClientConnectedEvent: " + ((ClientConnectedEvent) event).getClient() + " from: " + dispatcher);
		CatDogExample.sendChoiceRequest((ServerClient) ((ClientConnectedEvent) event).getClient()); // See "Send Packets"
	}
	
	@EventHandler
	public void onServerWriteFailed(WriteFailedPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Server WriteFailedPacketEvent failed: " + event.getException() + " from: " + dispatcher);
	}

	@EventHandler
	public void onServerWriteSuccess(WriteSuccessPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Server WriteSuccessPacketEvent: " + event.getPacket() + " from: " + dispatcher);
	}

	@EventHandler
	public void onServerReadFailed(ReadFailedPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Server ReadFailedPacketEvent failed: " + event.getException() + " from: " + dispatcher);
	}

	@EventHandler
	public void onServerReadSuccess(ReadSuccessPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Server ReadSuccessPacketEvent: " + event.getPacket() + " from: " + dispatcher);
	}

}