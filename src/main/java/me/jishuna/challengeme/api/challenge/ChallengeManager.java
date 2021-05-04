package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.event.ChallengeSetupEvent;
import me.jishuna.challengeme.challenges.AlwaysGlidingChallenge;
import me.jishuna.challengeme.challenges.AnimalLoverChallenge;
import me.jishuna.challengeme.challenges.AquaticChallenge;
import me.jishuna.challengeme.challenges.ChunkEffectChallenge;
import me.jishuna.challengeme.challenges.DoublePainChallenge;
import me.jishuna.challengeme.challenges.EffectMasterChallenge;
import me.jishuna.challengeme.challenges.EndermanChallenge;
import me.jishuna.challengeme.challenges.InvisibleMobsChallenge;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.challenges.NoJumpChallenge;
import me.jishuna.challengeme.challenges.NoStoppingChallenge;
import me.jishuna.challengeme.challenges.VampireChallenge;
import me.jishuna.challengeme.challenges.VegitarianChallenge;
import net.md_5.bungee.api.ChatColor;

public class ChallengeManager {

	private Map<String, Challenge> challenges = new LinkedHashMap<>();

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

		Map<String, Challenge> tempMap = new LinkedHashMap<>();
		event.getChallengesToAdd().forEach(challenge -> tempMap.put(challenge.getKey(), challenge));

		// TODO: Sort map by entry.getName, is there a better way to do this?
		List<Entry<String, Challenge>> list = new ArrayList<>(tempMap.entrySet());

		list.sort((Entry<String, Challenge> entryA, Entry<String, Challenge> entryB) -> ChatColor
				.stripColor(entryA.getValue().getName()).compareTo(ChatColor.stripColor(entryB.getValue().getName())));

		for (Entry<String, Challenge> entry : list) {
			this.challenges.put(entry.getKey(), entry.getValue());
		}

		this.challengeCache = new ArrayList<Challenge>(this.challenges.values().stream()
				.filter(challenge -> challenge.isEnabled()).collect(Collectors.toList()));
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
		this.defaultChallenges.add(new AlwaysGlidingChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new ChunkEffectChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new NoStoppingChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new EffectMasterChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new InvisibleMobsChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new EndermanChallenge(plugin, challengeConfig));
		this.defaultChallenges.add(new AquaticChallenge(plugin, challengeConfig));
	}

	public Optional<Challenge> getChallenge(String key) {
		return Optional.ofNullable(this.challenges.get(key));
	}

	public List<Challenge> getChallengeCache() {
		return challengeCache;
	}

	public Collection<Challenge> getAllChallenges() {
		return this.challenges.values();
	}
}
