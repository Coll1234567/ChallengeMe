package me.jishuna.challengeme.challenges;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class DoublePainChallenge extends Challenge {
	private static final String KEY = "double_pain";

	public DoublePainChallenge(Plugin owner) {
		super(owner, KEY);

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event, ChallengePlayer challengePlayer) {
		event.setDamage(event.getDamage() * 2);
	}
}
