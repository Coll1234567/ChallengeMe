package me.jishuna.challengeme.api.challenge;

import org.bukkit.entity.Player;

import me.jishuna.challengeme.api.player.ChallengePlayer;

public interface TickingChallenge {
	
	public void onTick(ChallengePlayer challengePlayer, Player player);

}
