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
import me.jishuna.challengeme.challenges.EffectMasterChallenge;
import me.jishuna.challengeme.challenges.EndermanChallenge;
import me.jishuna.challengeme.challenges.InvisibleMobsChallenge;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.challenges.NoJumpChallenge;
import me.jishuna.challengeme.challenges.NoStoppingChallenge;
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

		YamlConfiguration challengeConfig = this.plugin.getChallengeConfig();

		defaultChallenges.add(new NoDamageChallenge(plugin, challengeConfig));
		defaultChallenges.add(new VegitarianChallenge(plugin, challengeConfig));
		defaultChallenges.add(new AnimalLoverChallenge(plugin, challengeConfig));
		defaultChallenges.add(new VampireChallenge(plugin, challengeConfig));
		defaultChallenges.add(new NoJumpChallenge(plugin, challengeConfig));
		defaultChallenges.add(new DoublePainChallenge(plugin, challengeConfig));
		defaultChallenges.add(new AlwaysGlidingChallenge(plugin, challengeConfig));
		defaultChallenges.add(new ChunkEffectChallenge(plugin, challengeConfig));
		defaultChallenges.add(new NoStoppingChallenge(plugin, challengeConfig));
		defaultChallenges.add(new EffectMasterChallenge(plugin, challengeConfig));
		defaultChallenges.add(new InvisibleMobsChallenge(plugin, challengeConfig));
		defaultChallenges.add(new EndermanChallenge(plugin, challengeConfig));
		defaultChallenges.add(new AquaticChallenge(plugin, challengeConfig));
		defaultChallenges.add(new SpeedChallenge(plugin, challengeConfig));
		defaultChallenges.add(new BlockEffectChallenge(plugin, challengeConfig));
		defaultChallenges.add(new BouncyChallenge(plugin, challengeConfig));

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
