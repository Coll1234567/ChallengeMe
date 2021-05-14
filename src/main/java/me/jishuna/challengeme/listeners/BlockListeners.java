package me.jishuna.challengeme.listeners;

import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;

public class BlockListeners implements Listener {

	private PlayerManager playerManager;

	public BlockListeners(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(BlockBreakEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(BlockPlaceEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

}
