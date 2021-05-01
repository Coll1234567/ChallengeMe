package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;

public class DoublePainChallenge extends Challenge {

	public DoublePainChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "double-pain", messageConfig);

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event, Player player) {
		event.setDamage(event.getDamage() * 2);
	}
}
