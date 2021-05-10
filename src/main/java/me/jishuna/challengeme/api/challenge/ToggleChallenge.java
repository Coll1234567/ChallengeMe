package me.jishuna.challengeme.api.challenge;

import org.bukkit.entity.Player;

import me.jishuna.challengeme.api.player.ChallengePlayer;

public interface ToggleChallenge {
	public void onEnable(ChallengePlayer challengePlayer, Player player);

	public void onDisable(ChallengePlayer challengePlayer, Player player);
}
