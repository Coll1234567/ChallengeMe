package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.event.CategorySetupEvent;
import me.jishuna.challengeme.api.event.ChallengeSetupEvent;
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

	public ChallengeManager(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	public void reloadCategories() {
		CategorySetupEvent event = new CategorySetupEvent();

		YamlConfiguration categoryConfig = this.plugin.getCateogryConfig();
		for (String key : categoryConfig.getKeys(false)) {
			event.getCategoriesToAdd().add(new Category(key, categoryConfig.getConfigurationSection(key)));
		}

		Bukkit.getPluginManager().callEvent(event);

		event.getCategoriesToAdd().forEach(category -> this.catergories.put(category.getKey(), category));
	}

	public void reloadChallenges() {
		this.challenges.clear();

		ChallengeSetupEvent event = new ChallengeSetupEvent();
		event.getChallengesToAdd().addAll(this.getDefaultChallenges());
		Bukkit.getPluginManager().callEvent(event);

		event.getChallengesToAdd().forEach(challenge -> {
			this.challenges.put(challenge.getKey(), challenge);

			getCategory(challenge.getCategory()).ifPresent(category -> {
				List<Challenge> challengeList = this.categoryChallengeMap.computeIfAbsent(category,
						key -> new ArrayList<>());
				challengeList.add(challenge);
			});
		});

		this.categoryChallengeMap.values().forEach(list -> list.sort((challengeA, challengeB) -> ChatColor
				.stripColor(challengeA.getName()).compareTo(ChatColor.stripColor(challengeB.getName()))));
	}

	private List<Challenge> getDefaultChallenges() {
		List<Challenge> defaultChallenges = new ArrayList<>();

		defaultChallenges.add(new NoDamageChallenge(plugin));
		defaultChallenges.add(new VegitarianChallenge(plugin));
		defaultChallenges.add(new AnimalLoverChallenge(plugin));
		defaultChallenges.add(new VampireChallenge(plugin));
		defaultChallenges.add(new NoJumpingChallenge(plugin));
		defaultChallenges.add(new DoublePainChallenge(plugin));
		defaultChallenges.add(new AlwaysGlidingChallenge(plugin));
		defaultChallenges.add(new ChunkEffectChallenge(plugin));
		defaultChallenges.add(new NoStoppingChallenge(plugin));
		defaultChallenges.add(new RandomEffectsChallenge(plugin));
		defaultChallenges.add(new InvisibleMobsChallenge(plugin));
		defaultChallenges.add(new EndermanChallenge(plugin));
		defaultChallenges.add(new AquaticChallenge(plugin));
		defaultChallenges.add(new SpeedChallenge(plugin));
		defaultChallenges.add(new BlockEffectChallenge(plugin));
		defaultChallenges.add(new BouncyChallenge(plugin));
		defaultChallenges.add(new ReverseGravityChallenge(plugin));
		defaultChallenges.add(new NoDarknessChallenge(plugin));
		defaultChallenges.add(new NoRegenChallenge(plugin));

		return defaultChallenges;
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
		return this.categoryChallengeMap.get(category);
	}
}
