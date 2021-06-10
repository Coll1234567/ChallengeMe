package me.jishuna.challengeme.api.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.commonlib.events.EventConsumer;

public class EventManager {
	private final ChallengeMe plugin;

	private Map<Class<? extends Event>, EventConsumer<? extends Event>> listenerMap = new HashMap<>();

	public EventManager(ChallengeMe plugin) {
		this.plugin = plugin;
		registerBaseEvents();
	}

	public <T extends Event> boolean registerListener(Class<T> eventClass, Consumer<T> handler) {
		if (isListenerRegistered(eventClass))
			return false;

		EventConsumer<? extends Event> consumer = new EventConsumer<>(eventClass, handler);
		consumer.register(this.plugin);

		this.listenerMap.put(eventClass, consumer);
		return true;
	}

	public boolean isListenerRegistered(Class<? extends Event> eventClass) {
		return this.listenerMap.containsKey(eventClass);
	}

	public <T extends PlayerEvent> void processPlayerEvent(T event, Class<T> eventClass) {
		processEvent(event.getPlayer().getUniqueId(), event, eventClass);
	}

	public <T extends EntityEvent> void processEntityEvent(T event, Class<T> eventClass) {
		if (event.getEntityType() == EntityType.PLAYER) {
			processEvent(event.getEntity().getUniqueId(), event, eventClass);
		}
	}

	public <T extends Event> void processEvent(UUID id, T event, Class<T> eventClass) {
		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager().getPlayer(id);

		if (playerOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenge.getEventHandlers(eventClass).forEach(consumer -> consumer.consume(event, challengePlayer));
			}
		}
	}

	private void registerBaseEvents() {
		// Blocks
		registerListener(BlockBreakEvent.class,
				event -> processEvent(event.getPlayer().getUniqueId(), event, BlockBreakEvent.class));
		registerListener(BlockPlaceEvent.class,
				event -> processEvent(event.getPlayer().getUniqueId(), event, BlockPlaceEvent.class));

		// Player
		registerListener(PlayerRespawnEvent.class, event -> processPlayerEvent(event, PlayerRespawnEvent.class));
		registerListener(PlayerDeathEvent.class, event -> processEntityEvent(event, PlayerDeathEvent.class));
		registerListener(EntityToggleGlideEvent.class, event -> processEntityEvent(event, EntityToggleGlideEvent.class));
		registerListener(EntityAirChangeEvent.class, event -> processEntityEvent(event, EntityAirChangeEvent.class));
		registerListener(EntityPotionEffectEvent.class, event -> processEntityEvent(event, EntityPotionEffectEvent.class));
		registerListener(EntityRegainHealthEvent.class, event -> processEntityEvent(event, EntityRegainHealthEvent.class));

		registerListener(PlayerInteractEvent.class, event -> {
			if (event.getItem() != null && !event.getItem().getType().isAir()) {
				processEvent(event.getPlayer().getUniqueId(), event, PlayerInteractEvent.class);
			}
		});

		registerListener(PlayerItemConsumeEvent.class, event -> {
			if (event.getItem() != null && !event.getItem().getType().isAir()) {
				processEvent(event.getPlayer().getUniqueId(), event, PlayerItemConsumeEvent.class);
			}
		});

		// Combat
		registerListener(EntityDamageEvent.class, event -> processEntityEvent(event, EntityDamageEvent.class));

		registerListener(EntityDamageByEntityEvent.class, event -> {
			if (event.getEntity().getType() == EntityType.PLAYER) {
				processEvent(event.getEntity().getUniqueId(), event, EntityDamageByEntityEvent.class);
			}
			if (event.getDamager().getType() == EntityType.PLAYER) {
				processEvent(event.getDamager().getUniqueId(), event, EntityDamageByEntityEvent.class);
			} else if (event.getDamager() instanceof Projectile
					&& ((Projectile) event.getDamager()).getShooter() instanceof Player) {
				Player player = (Player) ((Projectile) event.getDamager()).getShooter();
				processEvent(player.getUniqueId(), event, EntityDamageByEntityEvent.class);
			}
		});
	}

}
