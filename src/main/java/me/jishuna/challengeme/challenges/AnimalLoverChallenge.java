package me.jishuna.challengeme.challenges;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class AnimalLoverChallenge extends Challenge {
	private static final String KEY = "animal_lover";

	public AnimalLoverChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));

		addEventHandler(EntityDamageByEntityEvent.class, this::onAttack);
	}

	private void onAttack(EntityDamageByEntityEvent event, ChallengePlayer challengePlayer) {
		if (event.getEntity() instanceof Animals || event.getEntity() instanceof Fish) {
			event.setCancelled(true);

			Player player = challengePlayer.getPlayer();

			if (player != null) {
				player.sendMessage(this.getMessage());
			}
		}
	}
}
