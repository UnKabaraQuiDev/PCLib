package catdog;

import lu.pcy113.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.pcy113.pclib.listener.EventDispatcher;
import lu.pcy113.pclib.listener.EventHandler;
import lu.pcy113.pclib.listener.EventListener;
import lu.pcy113.pclib.listener.EventManager;
import lu.pcy113.pclib.logger.GlobalLogger;

public class ClientEventListener implements EventListener {

	@EventHandler
	public void onClientConnect(ClientConnectedEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Client ClientConnectedEvent: " + ((ClientConnectedEvent) event).getClient() + " from: " + dispatcher);
	}

	@EventHandler
	public void onClientWriteFailed(WriteFailedPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Client WritePacketEvent failed: " + event.getException() + " from: " + dispatcher);
	}

	@EventHandler
	public void onClientWriteSuccess(WriteSuccessPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Client WriteSuccessPacketEvent: " + event.getPacket() + " from: " + dispatcher);
	}

	@EventHandler
	public void onClientReadFailed(ReadFailedPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Client ReadFailedPacketEvent failed: " + event.getException() + " from: " + dispatcher);
	}

	@EventHandler
	public void onClientReadSuccess(ReadSuccessPacketEvent event, EventManager em, EventDispatcher dispatcher) {
		GlobalLogger.info("Client ReadSuccessPacketEvent: " + event.getPacket() + " from: " + dispatcher);
	}

}