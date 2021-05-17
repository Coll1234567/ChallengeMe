package me.jishuna.challengeme.listeners;

import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;

public class PlayerListeners implements Listener {

	private PlayerManager playerManager;

	public PlayerListeners(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() == null || event.getItem().getType().isAir())
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(PlayerInteractEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(PlayerDeathEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(PlayerRespawnEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getItem() == null || event.getItem().getType().isAir())
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(PlayerItemConsumeEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(EntityRegainHealthEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onToggleGlide(EntityToggleGlideEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(EntityToggleGlideEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(EntityPotionEffectEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	@EventHandler
	public void onAirChange(EntityAirChangeEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(EntityAirChangeEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}
}
