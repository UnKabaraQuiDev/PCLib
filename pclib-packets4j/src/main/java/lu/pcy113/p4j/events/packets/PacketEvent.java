package lu.pcy113.p4j.events.packets;

import java.nio.ByteBuffer;

import lu.pcy113.p4j.P4JEndPoint;
import lu.pcy113.p4j.events.P4JEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PostPacketEvent.FailedPacketEvent;
import lu.pcy113.p4j.events.packets.PacketEvent.PostPacketEvent.SuccessPacketEvent;
import lu.pcy113.p4j.packets.Packet;
import lu.pcy113.p4j.socket.P4JInstance;
import lu.pcy113.p4j.socket.client.P4JClient;

public interface PacketEvent extends P4JEvent {

	P4JEndPoint getEndPoint();

	P4JInstance getInstance();

	Packet getPacket();

	ByteBuffer getContent();

	public static interface PostPacketEvent extends PacketEvent {

		public static interface SuccessPacketEvent {

		}

		public static class FailedPacketEvent {

			private Throwable e;

			public FailedPacketEvent(Throwable e) {
				this.e = e;
			}

			public Throwable getException() {
				return e;
			}

		}

	}

	public static interface PrePacketEvent extends PacketEvent {

	}

	public static interface ReadPacketEvent extends PacketEvent {

	}

	public static interface WritePacketEvent extends PacketEvent {

	}

	public static class PreReadPacketEvent implements ReadPacketEvent, PrePacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public PreReadPacketEvent(P4JEndPoint endPoint, P4JInstance instance, Packet packet, ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

	public static class ReadSuccessPacketEvent implements ReadPacketEvent, SuccessPacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public ReadSuccessPacketEvent(P4JEndPoint endPoint, P4JInstance instance, Packet packet, ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

	public static class ReadFailedPacketEvent extends FailedPacketEvent implements ReadPacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public ReadFailedPacketEvent(P4JEndPoint endPoint, P4JInstance instance, Throwable e, Packet packet, ByteBuffer content) {
			super(e);
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

	public static class PreWritePacketEvent implements WritePacketEvent, PrePacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public PreWritePacketEvent(P4JEndPoint endPoint, P4JInstance instance, Packet packet, ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

	public static class WriteSuccessPacketEvent implements WritePacketEvent, SuccessPacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public WriteSuccessPacketEvent(P4JEndPoint endPoint, P4JInstance instance, Packet packet, ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

	public static class WriteFailedPacketEvent extends FailedPacketEvent implements WritePacketEvent {

		private P4JEndPoint endPoint;
		private P4JInstance instance;
		private Packet packet;
		private ByteBuffer content;

		public WriteFailedPacketEvent(P4JEndPoint endPoint, P4JInstance instance, Throwable e, Packet packet, ByteBuffer content) {
			super(e);
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return instance;
		}

		@Override
		public Packet getPacket() {
			return packet;
		}

		@Override
		public ByteBuffer getContent() {
			return content;
		}

	}

}
