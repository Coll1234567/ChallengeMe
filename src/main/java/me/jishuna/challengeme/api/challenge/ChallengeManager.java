package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.event.ChallengeSetupEvent;
import me.jishuna.challengeme.challenges.AlwaysFlyingChallenge;
import me.jishuna.challengeme.challenges.AnimalLoverChallenge;
import me.jishuna.challengeme.challenges.DoublePainChallenge;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.challenges.NoJumpChallenge;
import me.jishuna.challengeme.challenges.VampireChallenge;
import me.jishuna.challengeme.challenges.VegitarianChallenge;

public class ChallengeManager {

	private final Map<String, Challenge> challenges = new TreeMap<>(
			(stringA, stringB) -> ChatColor.stripColor(stringA).compareTo(ChatColor.stripColor(stringB)));

	private List<Challenge> challengeCache;
	private final List<Challenge> defaultChallenges = new ArrayList<>();
	private final ChallengeMe plugin;

	public ChallengeManager(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	public void reloadChallenges() {
		setupDefaultChallenges();
		this.challenges.clear();

		ChallengeSetupEvent event = new ChallengeSetupEvent();
		event.getChallengesToAdd().addAll(this.defaultChallenges);
		Bukkit.getPluginManager().callEvent(event);

		event.getChallengesToAdd().forEach(challenge -> {
			if (challenge.isEnabled()) {
				this.challenges.put(challenge.getKey(), challenge);
			}
		});

		this.challengeCache = new ArrayList<Challenge>(this.challenges.values());
	}

	private void setupDefaultChallenges() {
		this.defaultChallenges.clear();

		YamlConfiguration challengeConfig = this.plugin.getChallengeConfig();

		this.defaultChallenges.add(new NoDamageChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new VegitarianChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new AnimalLoverChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new VampireChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new NoJumpChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new DoublePainChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new AlwaysFlyingChallenge(plugin, challengeConfig));

	}

	public Optional<Challenge> getChallenge(String key) {
		return Optional.ofNullable(this.challenges.get(key));
	}

	public List<Challenge> getChallengeCache() {
		return challengeCache;
	}
}
