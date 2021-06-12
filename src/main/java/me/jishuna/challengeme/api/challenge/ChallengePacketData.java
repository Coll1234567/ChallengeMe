package me.jishuna.challengeme.api.challenge;

import java.util.Collection;

import com.comphenix.protocol.PacketType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.challengeme.api.packets.PacketWrapper;

public class ChallengePacketData {
	private final Multimap<PacketType, PacketWrapper> packetMap = ArrayListMultimap.create();
	
	public void addPacketHandler(PacketWrapper wrapper) {
		this.packetMap.put(wrapper.getType(), wrapper);
	}

	public Collection<PacketWrapper> getPacketHandlers(PacketType type) {
		return this.packetMap.get(type);
	}
}
