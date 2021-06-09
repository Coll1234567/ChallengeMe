package me.jishuna.challengeme.challenges;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class RandomEffectsChallenge extends Challenge implements TickingChallenge {

	private List<PotionEffectType> effects;
	private final Map<UUID, Long> effectCache = new HashMap<>();
	private int maxLevel;
	private final Random random = new Random();
	private static final String KEY = "random_effects";

	public RandomEffectsChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);
		
		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());

		this.maxLevel = upgradeConfig.getInt("max-level", 3);

		for (String effect : upgradeConfig.getStringList("blacklisted-effects")) {
			PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

			if (type != null) {
				this.effects.remove(type);
			}
		}
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		UUID id = player.getUniqueId();
		Long time = this.effectCache.computeIfAbsent(id, key -> System.currentTimeMillis() + 59 * 1000);

		if (time <= System.currentTimeMillis()) {
			player.addPotionEffect(new PotionEffect(this.effects.get(random.nextInt(this.effects.size())), 60 * 20,
					random.nextInt(this.maxLevel), true));

			this.effectCache.put(id, System.currentTimeMillis() + 59 * 1000);
		}
	}
}
