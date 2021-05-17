package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class NoRegenChallenge extends Challenge implements ToggleChallenge {

	public NoRegenChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		super(owner, "no-regen", challengeConfig);

		addEventHandler(EntityRegainHealthEvent.class, this::onRegen);
	}

	private void onRegen(EntityRegainHealthEvent event, ChallengePlayer challengePlayer) {
		if (event.getRegainReason() == RegainReason.SATIATED) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		player.setSaturatedRegenRate(Integer.MAX_VALUE);
	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
		player.setSaturatedRegenRate(10);

	}
}
