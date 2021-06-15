package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.ChallengeMeAPI;
import me.jishuna.challengeme.challenges.AlwaysGlidingChallenge;
import me.jishuna.challengeme.challenges.AnimalLoverChallenge;
import me.jishuna.challengeme.challenges.AquaticChallenge;
import me.jishuna.challengeme.challenges.BlockEffectChallenge;
import me.jishuna.challengeme.challenges.BouncyChallenge;
import me.jishuna.challengeme.challenges.ChunkEffectChallenge;
import me.jishuna.challengeme.challenges.DoublePainChallenge;
import me.jishuna.challengeme.challenges.EndermanChallenge;
import me.jishuna.challengeme.challenges.InvisibleMobsChallenge;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.challenges.NoDarknessChallenge;
import me.jishuna.challengeme.challenges.NoJumpingChallenge;
import me.jishuna.challengeme.challenges.NoRegenChallenge;
import me.jishuna.challengeme.challenges.NoStoppingChallenge;
import me.jishuna.challengeme.challenges.RandomEffectsChallenge;
import me.jishuna.challengeme.challenges.ReverseGravityChallenge;
import me.jishuna.challengeme.challenges.SpeedChallenge;
import me.jishuna.challengeme.challenges.VampireChallenge;
import me.jishuna.challengeme.challenges.VegitarianChallenge;
import net.md_5.bungee.api.ChatColor;

public class ChallengeManager {

	private Map<String, Challenge> challenges = new HashMap<>();
	private Map<String, Category> catergories = new LinkedHashMap<>();
	private Map<Category, List<Challenge>> categoryChallengeMap = new HashMap<>();

	private final ChallengeMe plugin;

	private boolean hasForcedChallenges = false;

	public ChallengeManager(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	public void reload() {
		YamlConfiguration categoryConfig = this.plugin.getCateogryConfig();

		this.catergories.values().forEach(category -> category.reload(categoryConfig));
		this.challenges.values().forEach(Challenge::reload);

		this.categoryChallengeMap.clear();
		this.challenges.values().forEach(this::setCategory);
	}

	public void registerChallenge(Challenge challenge) {
		this.challenges.put(challenge.getKey(), challenge);

		if (challenge.isForced()) {
			this.hasForcedChallenges = true;
		}

		setCategory(challenge);
	}

	private void setCategory(Challenge challenge) {
		getCategory(challenge.getCategory()).ifPresent(category -> {
			List<Challenge> challengeList = this.categoryChallengeMap.computeIfAbsent(category,
					key -> new ArrayList<>());
			challengeList.add(challenge);

			challengeList.sort((challengeA, challengeB) -> ChatColor.stripColor(challengeA.getName())
					.compareTo(ChatColor.stripColor(challengeB.getName())));
		});
	}

	public void registerCategory(Category category) {
		this.catergories.put(category.getKey(), category);
	}

	public void loadDefaults() {
		YamlConfiguration categoryConfig = this.plugin.getCateogryConfig();
		for (String key : categoryConfig.getKeys(false)) {
			registerCategory(new Category(key, categoryConfig));
		}

		registerChallenge(new NoDamageChallenge(plugin));
		registerChallenge(new VegitarianChallenge(plugin));
		registerChallenge(new AnimalLoverChallenge(plugin));
		registerChallenge(new VampireChallenge(plugin));
		registerChallenge(new NoJumpingChallenge(plugin));
		registerChallenge(new DoublePainChallenge(plugin));
		registerChallenge(new AlwaysGlidingChallenge(plugin));
		registerChallenge(new ChunkEffectChallenge(plugin));
		registerChallenge(new NoStoppingChallenge(plugin));
		registerChallenge(new RandomEffectsChallenge(plugin));
		registerChallenge(new EndermanChallenge(plugin));
		registerChallenge(new SpeedChallenge(plugin));
		registerChallenge(new BlockEffectChallenge(plugin));
		registerChallenge(new BouncyChallenge(plugin));
		registerChallenge(new ReverseGravityChallenge(plugin));
		registerChallenge(new NoDarknessChallenge(plugin));
		registerChallenge(new NoRegenChallenge(plugin));

		if (ChallengeMeAPI.hasProtcolLib()) {
			registerChallenge(new AquaticChallenge(plugin));
			registerChallenge(new InvisibleMobsChallenge(plugin));
		}

	}

	public Optional<Challenge> getChallenge(String key) {
		return Optional.ofNullable(this.challenges.get(key));
	}

	public Optional<Category> getCategory(String key) {
		return Optional.ofNullable(this.catergories.get(key));
	}

	public Collection<Challenge> getAllChallenges() {
		return this.challenges.values();
	}

	public Collection<Category> getCategories() {
		return this.catergories.values();
	}

	public Collection<Challenge> getChallenges(Category category) {
		return this.categoryChallengeMap.getOrDefault(category, Collections.emptyList());
	}

	public boolean hasForcedChallenges() {
		return hasForcedChallenges;
	}
}
