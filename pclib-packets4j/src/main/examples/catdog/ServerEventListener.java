package catdog;

import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.pcy113.p4j.socket.server.ServerClient;
import lu.pcy113.pclib.listener.EventDispatcher;
import lu.pcy113.pclib.listener.EventHandler;
import lu.pcy113.pclib.listener.EventListener;
import lu.pcy113.pclib.listener.EventManager;
import lu.pcy113.pclib.logger.GlobalLogger;

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