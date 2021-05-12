package me.jishuna.challengeme.challenges;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class BlockEffectChallenge extends Challenge {

	private final List<PotionEffectType> effects;
	private final int maxLevel;
	private final int duration;
	private final int chance;
	private final Random random = new Random();

	public BlockEffectChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("block-effects"));
	}

	private BlockEffectChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "block-effects", challengeSection);

		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());

		this.maxLevel = challengeSection.getInt("max-level", 3);
		this.duration = challengeSection.getInt("duration", 100);
		this.chance = challengeSection.getInt("chance", 10);

		for (String effect : challengeSection.getStringList("blacklisted-effects")) {
			PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

			if (type != null) {
				this.effects.remove(type);
			}
		}

		addEventHandler(BlockBreakEvent.class, this::onBreakBlock);
	}

	private void onBreakBlock(BlockBreakEvent event, ChallengePlayer challengePlayer) {
		if (random.nextInt(100) < this.chance) {
			Player player = challengePlayer.getPlayer();

			player.addPotionEffect(new PotionEffect(this.effects.get(random.nextInt(this.effects.size())),
					this.duration, random.nextInt(this.maxLevel), true));
		}
	}

}
