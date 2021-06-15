package me.jishuna.challengeme.challenges;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class VampireChallenge extends Challenge implements TickingChallenge {
	private static final String KEY = "vampire";

	public VampireChallenge(Plugin owner) {
		super(owner, KEY);
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
