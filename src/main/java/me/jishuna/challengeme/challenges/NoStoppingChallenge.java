package me.jishuna.challengeme.challenges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;

public class NoStoppingChallenge extends Challenge implements TickingChallenge {

	private final Map<UUID, NoStoppingChallengeData> challengeData = new HashMap<>();
	private final int msNeeded;

	public NoStoppingChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("challenges.no-stopping"));
	}

	private NoStoppingChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "challenges.no-stopping", challengeSection);

		this.msNeeded = challengeSection.getInt("seconds", 3) * 1000;
	}

	@Override
	public void onTick(Player player) {
		UUID id = player.getUniqueId();
		NoStoppingChallengeData challengeData = this.challengeData.get(id);
		Location location = player.getLocation();

		if (challengeData == null) {
			challengeData = new NoStoppingChallengeData(player.getLocation());
			this.challengeData.put(id, challengeData);
		}

		if (!challengeData.compareLocations(location)) {
			challengeData.setLastLocation(location);
			challengeData.setTimestamp(System.currentTimeMillis() + msNeeded);
		}

		if (challengeData.getTimestamp() <= System.currentTimeMillis())
			player.setFireTicks(20);
	}
}
