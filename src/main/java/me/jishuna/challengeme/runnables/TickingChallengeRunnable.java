package me.jishuna.challengeme.runnables;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class TickingChallengeRunnable extends BukkitRunnable {

	private final ChallengeMe plugin;

	public TickingChallengeRunnable(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Optional<ChallengePlayer> challengePlayerOptional = plugin.getPlayerManager().getPlayer(player);

			if (challengePlayerOptional.isPresent()) {
				ChallengePlayer challengePlayer = challengePlayerOptional.get();
				if (!challengePlayer.isLoaded())
					continue;
				
				challengePlayer.getActiveChallenges().forEach(challenge -> {
					if (challenge instanceof TickingChallenge) {
						((TickingChallenge) challenge).onTick(challengePlayer, player);
					}
				});
			}
		}

	}

}
