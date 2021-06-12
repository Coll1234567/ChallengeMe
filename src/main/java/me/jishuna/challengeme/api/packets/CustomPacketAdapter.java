package me.jishuna.challengeme.api.packets;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;

public class CustomPacketAdapter extends PacketAdapter {

	private final PlayerManager playerManager;
	private final PacketType type;

	private boolean handleSend = false;
	private boolean handleRecieve = false;

	public CustomPacketAdapter(ChallengeMe plugin, PacketType type) {
		super(plugin, ListenerPriority.NORMAL, type);

		this.type = type;
		this.playerManager = plugin.getPlayerManager();
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if (!handleSend)
			return;

		Player player = event.getPlayer();
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getChallengePacketData().getPacketHandlers(this.type)
						.forEach(consumer -> consumer.consumeSend(event));
			}
		}
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		if (!handleRecieve)
			return;

		Player player = event.getPlayer();
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getChallengePacketData().getPacketHandlers(this.type)
						.forEach(consumer -> consumer.consumeSend(event));
			}
		}
	}

	public boolean shouldHandleSend() {
		return handleSend;
	}

	public void setHandleSend(boolean handleSend) {
		this.handleSend = handleSend;
	}

	public boolean shouldHandleRecieve() {
		return handleRecieve;
	}

	public void setHandleRecieve(boolean handleRecieve) {
		this.handleRecieve = handleRecieve;
	}

}
