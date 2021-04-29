package me.jishuna.challengeme;

import java.util.Optional;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.jishuna.challengeme.api.Challenge;
import me.jishuna.challengeme.api.ChallengePlayer;
import me.jishuna.challengeme.api.PlayerManager;

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
			for (Challenge challenge : playerOptional.get().getActiveChallenges()) {
				challenge.getEventHandlers(EntityDamageEvent.class).forEach(consumer -> consumer.consume(event));
			}
		}
	}

}
