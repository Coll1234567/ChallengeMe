package me.jishuna.challengeme.packets;

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

public class PacketAdapterLivingSpawn extends PacketAdapter {

	private final PlayerManager playerManager;

	@Override
	public void onPacketSending(PacketEvent event) {
		Player player = event.getPlayer();
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getPacketHandlers(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
						.forEach(consumer -> consumer.consumeSend(event));
			}
		}
	}

	public PacketAdapterLivingSpawn(ChallengeMe plugin, ListenerPriority listenerPriority) {
		super(plugin, listenerPriority, PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		this.playerManager = plugin.getPlayerManager();
	}

}
