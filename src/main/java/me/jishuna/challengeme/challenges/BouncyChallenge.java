package me.jishuna.challengeme.challenges;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class BouncyChallenge extends Challenge implements TickingChallenge {
	private static final String KEY = "bouncy";

	public BouncyChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));
	}

	//TODO come up with new method of checking if the player is on ground
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
