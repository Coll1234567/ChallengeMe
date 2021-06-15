package me.jishuna.challengeme.challenges;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class NoDamageChallenge extends Challenge {
	private static final String KEY = "no_damage";

	public NoDamageChallenge(Plugin owner) {
		super(owner, KEY);

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event, ChallengePlayer challengePlayer) {
		Player player = (Player) event.getEntity();
		if (event.getDamage() > 0.0d) {
			// TODO Can we improve this while still keeping the correct death message?
			event.setDamage(10d);
			player.setHealth(0.001);
			player.sendMessage(this.getMessage());
		}
	}
}
