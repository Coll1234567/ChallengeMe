package me.jishuna.challengeme.api.packets;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.jishuna.challengeme.ChallengeMe;

public class PacketManager {
	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	private final ChallengeMe plugin;

	private Map<PacketType, CustomPacketAdapter> adapterMap = new HashMap<>();

	public PacketManager(ChallengeMe plugin) {
		this.plugin = plugin;
		registerBaseAdapters();
	}

	public void registerListener(PacketType type, PacketOptions options) {
		CustomPacketAdapter adapter = this.adapterMap.get(type);

		if (adapter == null) {
			adapter = new CustomPacketAdapter(this.plugin, type);
			manager.addPacketListener(adapter);
		}
		
		if (options == PacketOptions.HANDLE_SEND || options == PacketOptions.HANDLE_BOTH) {
			adapter.setHandleSend(true);
		}

		if (options == PacketOptions.HANDLE_RECIEVE || options == PacketOptions.HANDLE_BOTH) {
			adapter.setHandleRecieve(true);
		}
	}

	private void registerBaseAdapters() {
		registerListener(PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketOptions.HANDLE_SEND);
	}

}
