package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class BouncyChallenge extends Challenge implements TickingChallenge {

	public BouncyChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "bouncy", messageConfig);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		if (player.isOnGround()) {
			Vector velocity = player.getVelocity();
			velocity.setY(0.4);
			player.setVelocity(velocity);
		}
	}
}
