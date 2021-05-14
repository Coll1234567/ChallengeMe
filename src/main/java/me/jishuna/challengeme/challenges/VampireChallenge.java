package me.jishuna.challengeme.challenges;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class VampireChallenge extends Challenge implements TickingChallenge {

	public VampireChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		super(owner, "vampire", challengeConfig);
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		Location location = player.getLocation();
		if (location.getWorld().getTime() < 13000
				&& location.getWorld().getHighestBlockYAt(location) < location.getY()) {
			player.setFireTicks(20);
		}
	}
}
