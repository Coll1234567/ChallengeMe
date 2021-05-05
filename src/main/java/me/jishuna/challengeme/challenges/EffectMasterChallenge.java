package me.jishuna.challengeme.challenges;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;

public class EffectMasterChallenge extends Challenge implements TickingChallenge {

	private final List<PotionEffectType> effects;
	private final Map<UUID, Long> effectCache = new HashMap<>();
	private final int maxLevel;
	private final Random random = new Random();

	public EffectMasterChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("effect-master"));
	}

	private EffectMasterChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "effect-master", challengeSection);

		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());

		this.maxLevel = challengeSection.getInt("max-level", 3);

		for (String effect : challengeSection.getStringList("blacklisted-effects")) {
			PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

			if (type != null) {
				this.effects.remove(type);
			}
		}

	}

	@Override
	public void onTick(Player player) {
		UUID id = player.getUniqueId();
		Long time = this.effectCache.computeIfAbsent(id, key -> System.currentTimeMillis() + 59 * 1000);

		if (time <= System.currentTimeMillis()) {
			player.addPotionEffect(new PotionEffect(this.effects.get(random.nextInt(this.effects.size())), 60 * 20,
					random.nextInt(this.maxLevel), true, false));

			this.effectCache.put(id, System.currentTimeMillis() + 59 * 1000);
		}
	}
}
