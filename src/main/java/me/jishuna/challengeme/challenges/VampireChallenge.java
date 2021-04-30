package me.jishuna.challengeme.challenges;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;

public class VampireChallenge extends Challenge implements TickingChallenge {

	public VampireChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "vampire", messageConfig);
	}

	@Override
	public void onTick(Player player) {
		Location location = player.getLocation();
		if (location.getWorld().getTime() < 13000
				&& location.getWorld().getHighestBlockYAt(location) < location.getY()) {
			player.setFireTicks(20);
		}
	}
}
