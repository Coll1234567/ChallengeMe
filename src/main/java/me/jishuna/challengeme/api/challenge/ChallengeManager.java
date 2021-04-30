package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChallengeManager {

	private final Map<String, Challenge> challenges = new TreeMap<>(
			(stringA, stringB) -> ChatColor.stripColor(stringA).compareTo(ChatColor.stripColor(stringB)));
	private List<Challenge> challengeCache;

	public void registerChallenge(Challenge challenge, YamlConfiguration challengeConfig) {
		if (challengeConfig.getBoolean("challenges." + challenge.getKey() + ".enabled", true)) {
			this.challenges.putIfAbsent(challenge.getKey(), challenge);
			this.challengeCache = new ArrayList<Challenge>(this.challenges.values());
		}
	}

	public Optional<Challenge> getChallenge(String key) {
		return Optional.ofNullable(this.challenges.get(key));
	}

	public List<Challenge> getChallengeCache() {
		return challengeCache;
	}
}
