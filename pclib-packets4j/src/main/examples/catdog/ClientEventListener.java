package catdog;

import lu.kbra.p4j.events.client.P4JConnectionEvent.ClientConnectedEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.ReadSuccessPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteFailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.WriteSuccessPacketEvent;
import lu.kbra.pclib.listener.EventDispatcher;
import lu.kbra.pclib.listener.EventHandler;
import lu.kbra.pclib.listener.EventListener;
import lu.kbra.pclib.listener.EventManager;
import lu.kbra.pclib.logger.GlobalLogger;

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