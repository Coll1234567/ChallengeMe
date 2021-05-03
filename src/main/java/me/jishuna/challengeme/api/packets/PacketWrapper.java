package me.jishuna.challengeme.api.packets;

import java.util.function.Consumer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

public class PacketWrapper {

	private Consumer<PacketEvent> sendHandler;
	private Consumer<PacketEvent> recieveHandler;
	private final PacketType type;

	public PacketWrapper(PacketType type) {
		this.type = type;
	}

	public void setSendHandler(Consumer<PacketEvent> sendHandler) {
		this.sendHandler = sendHandler;
	}

	public void setRecieveHandler(Consumer<PacketEvent> recieveHandler) {
		this.recieveHandler = recieveHandler;
	}

	public PacketType getType() {
		return type;
	}

	public void consumeSend(PacketEvent event) {
		if (this.sendHandler != null && event.getPacketType().equals(this.type)) {
			this.sendHandler.accept(event);
		}
	}

	public void consumeRecieve(PacketEvent event) {
		if (this.recieveHandler != null && event.getPacketType().equals(this.type)) {
			this.recieveHandler.accept(event);
		}
	}
}
