package me.jishuna.challengeme.listeners;

import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;

public class ChallengeListener implements Listener {

	public ChallengeListener(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	private PlayerManager playerManager;

	@EventHandler
	public void onDamageEvent(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Player player = (Player) event.getEntity();
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getEventHandlers(EntityDamageEvent.class)
						.forEach(consumer -> consumer.consume(event, player));
			}
		}
	}

	@EventHandler
	public void onAttackEvent(EntityDamageByEntityEvent event) {
		Optional<ChallengePlayer> playerOptional = Optional.empty();
		Player player = null;

		if (event.getDamager().getType() == EntityType.PLAYER) {
			player = (Player) event.getDamager();
			playerOptional = playerManager.getPlayer(player.getUniqueId());
		} else if (event.getDamager() instanceof Projectile
				&& ((Projectile) event.getDamager()).getShooter() instanceof Player) {
			player = (Player) ((Projectile) event.getDamager()).getShooter();
			playerOptional = playerManager.getPlayer(player.getUniqueId());
		}

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				for (EventWrapper<? extends Event> consumer : challenge
						.getEventHandlers(EntityDamageByEntityEvent.class)) {
					consumer.consume(event, player);
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() == null || event.getItem().getType().isAir())
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getEventHandlers(PlayerInteractEvent.class)
						.forEach(consumer -> consumer.consume(event, event.getPlayer()));
			}
		}
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getItem() == null || event.getItem().getType().isAir())
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getPlayer().getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getEventHandlers(PlayerItemConsumeEvent.class)
						.forEach(consumer -> consumer.consume(event, event.getPlayer()));
			}
		}
	}

	@EventHandler
	public void onToggleGlide(EntityToggleGlideEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Player player = (Player) event.getEntity();
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getEventHandlers(EntityToggleGlideEvent.class)
						.forEach(consumer -> consumer.consume(event, player));
			}
		}
	}

}
