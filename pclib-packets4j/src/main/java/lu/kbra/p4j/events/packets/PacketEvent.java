package lu.kbra.p4j.events.packets;

import java.nio.ByteBuffer;

import lu.kbra.p4j.P4JEndPoint;
import lu.kbra.p4j.events.P4JEvent;
import lu.kbra.p4j.events.packets.PacketEvent.PostPacketEvent.FailedPacketEvent;
import lu.kbra.p4j.events.packets.PacketEvent.PostPacketEvent.SuccessPacketEvent;
import lu.kbra.p4j.packets.Packet;
import lu.kbra.p4j.socket.P4JInstance;

public interface PacketEvent extends P4JEvent {

	P4JEndPoint getEndPoint();

	P4JInstance getInstance();

	Packet getPacket();

	ByteBuffer getContent();

	public interface PostPacketEvent extends PacketEvent {

		public interface SuccessPacketEvent {

		}

		public static class FailedPacketEvent {

			private final Throwable e;

			public FailedPacketEvent(final Throwable e) {
				this.e = e;
			}

			public Throwable getException() {
				return this.e;
			}

		}

	}

	public interface PrePacketEvent extends PacketEvent {

	}

	public interface ReadPacketEvent extends PacketEvent {

	}

	public interface WritePacketEvent extends PacketEvent {

	}

	public static class PreReadPacketEvent implements ReadPacketEvent, PrePacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public PreReadPacketEvent(final P4JEndPoint endPoint, final P4JInstance instance, final Packet packet, final ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

	public static class ReadSuccessPacketEvent implements ReadPacketEvent, SuccessPacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public ReadSuccessPacketEvent(
				final P4JEndPoint endPoint,
				final P4JInstance instance,
				final Packet packet,
				final ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

	public static class ReadFailedPacketEvent extends FailedPacketEvent implements ReadPacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public ReadFailedPacketEvent(
				final P4JEndPoint endPoint,
				final P4JInstance instance,
				final Throwable e,
				final Packet packet,
				final ByteBuffer content) {
			super(e);
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

	public static class PreWritePacketEvent implements WritePacketEvent, PrePacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public PreWritePacketEvent(final P4JEndPoint endPoint, final P4JInstance instance, final Packet packet, final ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

	public static class WriteSuccessPacketEvent implements WritePacketEvent, SuccessPacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public WriteSuccessPacketEvent(
				final P4JEndPoint endPoint,
				final P4JInstance instance,
				final Packet packet,
				final ByteBuffer content) {
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

	public static class WriteFailedPacketEvent extends FailedPacketEvent implements WritePacketEvent {

		private final P4JEndPoint endPoint;
		private final P4JInstance instance;
		private final Packet packet;
		private final ByteBuffer content;

		public WriteFailedPacketEvent(
				final P4JEndPoint endPoint,
				final P4JInstance instance,
				final Throwable e,
				final Packet packet,
				final ByteBuffer content) {
			super(e);
			this.endPoint = endPoint;
			this.instance = instance;
			this.packet = packet;
			this.content = content;
		}

		@Override
		public P4JEndPoint getEndPoint() {
			return this.endPoint;
		}

		@Override
		public P4JInstance getInstance() {
			return this.instance;
		}

		@Override
		public Packet getPacket() {
			return this.packet;
		}

		@Override
		public ByteBuffer getContent() {
			return this.content;
		}

	}

}
