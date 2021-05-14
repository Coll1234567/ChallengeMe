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

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;

public class CombatListeners implements Listener {

	private PlayerManager playerManager;

	public CombatListeners(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}
	
	@EventHandler
	public void onDamageEvent(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;

		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(event.getEntity().getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(EntityDamageEvent.class)
						.forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}
	
	@EventHandler
	public void onAttackEvent(EntityDamageByEntityEvent event) {
		Optional<ChallengePlayer> playerOptional = Optional.empty();

		if (event.getDamager().getType() == EntityType.PLAYER) {

			playerOptional = playerManager.getPlayer(event.getDamager().getUniqueId());
		} else if (event.getDamager() instanceof Projectile
				&& ((Projectile) event.getDamager()).getShooter() instanceof Player) {
			Player player = (Player) ((Projectile) event.getDamager()).getShooter();
			playerOptional = playerManager.getPlayer(player.getUniqueId());
		}

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				for (EventWrapper<? extends Event> consumer : challenge
						.getEventHandlers(EntityDamageByEntityEvent.class)) {
					consumer.consume(event, challengePlayer);
				}
			}
		}
	}
}