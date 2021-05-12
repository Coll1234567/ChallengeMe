package me.jishuna.challengeme.listeners;

import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.commonlib.LocationUtils;

public class ChallengeListener implements Listener {

	public ChallengeListener(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	private PlayerManager playerManager;

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

//	@EventHandler
//	public void onMove(PlayerMoveEvent event) {
//
//		Player player = event.getPlayer();
//		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());
//
//		if (playerOptional.isPresent()) {
//			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
//				challenge.getEventHandlers(PlayerMoveEvent.class).forEach(consumer -> consumer.consume(event, player));
//			}
//		}
//	}

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
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		Player player = LocationUtils.getNearestPlayer(event.getEntity());
		if (player == null)
			return;
		
		Optional<ChallengePlayer> playerOptional = playerManager.getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(ExplosionPrimeEvent.class)
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
	public void onPotionEffect(EntityAirChangeEvent event) {
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
