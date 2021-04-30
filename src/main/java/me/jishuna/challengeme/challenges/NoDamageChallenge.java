package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;

public class NoDamageChallenge extends Challenge {

	public NoDamageChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "no-damage", messageConfig);

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event, Player player) {
		if (event.getDamage() > 0.0d) {
			event.setDamage(Integer.MAX_VALUE);
			player.sendMessage(this.getMessage());
		}
	}
}
