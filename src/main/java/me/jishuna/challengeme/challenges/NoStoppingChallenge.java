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
	private final int checksNeeded;

	public NoStoppingChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("challenges.no-stopping"));
	}

	private NoStoppingChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "challenges.no-stopping", challengeSection);

		this.checksNeeded = challengeSection.getInt("seconds", 3) * 2;
	}

	@Override
	public void onTick(Player player) {
		UUID id = player.getUniqueId();
		NoStoppingChallengeData challengeData = this.challengeData.get(id);
		Location location = player.getLocation();

		int checks;
		if (challengeData == null) {
			checks = 0;
			challengeData = new NoStoppingChallengeData(player.getLocation());
			this.challengeData.put(id, challengeData);
		} else {
			checks = challengeData.getChecks();
		}
		
		if (challengeData.compareLocations(location)) {
			if (checks < this.checksNeeded) {
			checks++;
			challengeData.setChecks(checks);
			}
		} else {
			challengeData.setLastLocation(location);
			challengeData.setChecks(0);
			return;
		}

		if (checks >= checksNeeded)
			player.setFireTicks(20);
	}
}
