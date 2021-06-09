package me.jishuna.challengeme.challenges;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class AlwaysGlidingChallenge extends Challenge implements TickingChallenge {
	private static final String KEY = "always_gliding";

	public AlwaysGlidingChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));

		addEventHandler(EntityToggleGlideEvent.class, this::onToggleGlide);
	}

	private void onToggleGlide(EntityToggleGlideEvent event, ChallengePlayer player) {
		event.setCancelled(true);
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		if (!player.isGliding() && !player.isFlying())
			player.setGliding(true);

		Vector velocity = player.getVelocity();
		if (!player.isFlying() && velocity.length() < 0.3D && !player.getLocation().getBlock().isLiquid())
			player.setVelocity(velocity.multiply(1.5D));

	}
}
