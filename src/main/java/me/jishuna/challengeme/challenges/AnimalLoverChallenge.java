package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class AnimalLoverChallenge extends Challenge {

	public AnimalLoverChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "animal-lover", messageConfig);

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
