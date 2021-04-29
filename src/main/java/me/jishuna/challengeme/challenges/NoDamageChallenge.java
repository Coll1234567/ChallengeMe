package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.Challenge;
import net.md_5.bungee.api.ChatColor;

public class NoDamageChallenge extends Challenge {

	private final String deathMessage;

	public NoDamageChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "no-damage", messageConfig);

		this.deathMessage = ChatColor.translateAlternateColorCodes('&',
				messageConfig.getString("challenges." + this.getKey() + ".death", ""));

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event) {
		if (event.getDamage() > 0.0d) {
			event.setDamage(Integer.MAX_VALUE);
			event.getEntity().sendMessage(this.deathMessage);
		}
	}
}
